package com.qiwenshare.ufop.cache.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;

import com.qiwenshare.ufop.cache.CacheService;
import com.qiwenshare.ufop.cache.CacheStats;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CacheServiceJDKImpl implements CacheService {

    private static final Cache<String, CacheValue> cache = Caffeine.newBuilder()
            .maximumSize(500000)
            .expireAfter(new DynamicExpiry())
            .recordStats()
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
        return value.getValue();
    }

    @Override
    public void set(String key, String value, long timeoutSeconds) {
        cache.put(key, new CacheValue(value, timeoutSeconds));
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
        return value.incrementAndGet();
    }

    @Override
    public CacheStats getCacheStats() {
        com.github.benmanes.caffeine.cache.stats.CacheStats stats = cache.stats();
        CacheStats cacheStats = new CacheStats();
        cacheStats.setHitCount(stats.hitCount());
        cacheStats.setMissCount(stats.missCount());
        cacheStats.setLoadSuccessCount(stats.loadSuccessCount());
        cacheStats.setLoadFailureCount(stats.loadFailureCount());
        cacheStats.setTotalLoadTime(stats.totalLoadTime());
        cacheStats.setEvictionCount(stats.evictionCount());
        cacheStats.setEstimatedSize(cache.estimatedSize());
        return cacheStats;
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

        public synchronized String getValue() {
            if (counter != null) {
                return String.valueOf(counter.get());
            }
            return value;
        }

        public long getTimeout() {
            return timeout;
        }

        public synchronized long incrementAndGet() {
            if (counter == null) {
                long initial = parseLongValue(value);
                counter = new AtomicLong(initial);
                value = null;
            }
            return counter.incrementAndGet();
        }

        private long parseLongValue(String val) {
            if (val == null) {
                return 0;
            }
            try {
                return Long.parseLong(val);
            } catch (NumberFormatException e) {
                return 0;
            }
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
