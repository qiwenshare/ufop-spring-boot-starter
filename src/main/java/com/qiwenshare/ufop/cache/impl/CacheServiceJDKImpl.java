package com.qiwenshare.ufop.cache.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.qiwenshare.ufop.cache.CacheService;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CacheServiceJDKImpl implements CacheService {

    private static final Cache<String, CacheValue> cache = Caffeine.newBuilder()
            .maximumSize(500000)
            .expireAfter(new DynamicExpiry())
            .build();

    @Override
    public void set(String key, String value) {
        cache.put(key, new CacheValue(value, 0));
    }

    @Override
    public String getObject(String key) {
        CacheValue value = cache.getIfPresent(key);
        if (value == null) {
            return null;
        }
        if (value.isCounter()) {
            return String.valueOf(value.getCounter().get());
        }
        return value.getValue();
    }

    @Override
    public void set(String key, String value, long time) {
        cache.put(key, new CacheValue(value, time));
    }

    @Override
    public boolean hasKey(String key) {
        return cache.getIfPresent(key) != null;
    }

    @Override
    public void deleteKey(String key) {
        cache.invalidate(key);
    }

    @Override
    public Long getIncr(String key) {
        CacheValue value = cache.get(key, k -> new CacheValue("0", 0));
        
        if (!value.isCounter()) {
            long current = parseLongValue(value.getValue());
            value.convertToCounter(current);
        }
        
        return value.getCounter().incrementAndGet();
    }

    private long parseLongValue(String value) {
        if (value == null) {
            return 0;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static class CacheValue {
        private volatile String value;
        private volatile AtomicLong counter;
        private final long timeout;

        public CacheValue(String value, long timeout) {
            this.value = value;
            this.timeout = timeout;
        }

        public String getValue() {
            return value;
        }

        public long getTimeout() {
            return timeout;
        }

        public boolean isCounter() {
            return counter != null;
        }

        public AtomicLong getCounter() {
            return counter;
        }

        public void convertToCounter(long initialValue) {
            this.counter = new AtomicLong(initialValue);
            this.value = null;
        }
    }

    private static class DynamicExpiry implements Expiry<String, CacheValue> {
        @Override
        public long expireAfterCreate(String key, CacheValue value, long currentTime) {
            if (value.getTimeout() > 0) {
                return TimeUnit.SECONDS.toNanos(value.getTimeout());
            }
            return Long.MAX_VALUE;
        }

        @Override
        public long expireAfterUpdate(String key, CacheValue value, long currentTime, long currentDuration) {
            return expireAfterCreate(key, value, currentTime);
        }

        @Override
        public long expireAfterRead(String key, CacheValue value, long currentTime, long currentDuration) {
            return currentDuration;
        }
    }
}
