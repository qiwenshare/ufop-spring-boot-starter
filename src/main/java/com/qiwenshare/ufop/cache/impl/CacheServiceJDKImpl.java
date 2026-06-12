package com.qiwenshare.ufop.cache.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.qiwenshare.ufop.cache.CacheKeyInfo;
import com.qiwenshare.ufop.cache.CacheService;
import com.qiwenshare.ufop.cache.CacheStats;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CacheServiceJDKImpl implements CacheService {

    private static final Cache<String, CacheValue> cache = Caffeine.newBuilder()
            .maximumSize(500000)
            .expireAfter(new DynamicExpiry())
            .recordStats()
            .build();
            
    private static final Cache<String, Boolean> missedKeys = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    @Override
    public void set(String key, String value) {
        cache.put(key, new CacheValue(value, 0));
    }

    @Override
    public String getObject(String key) {
        CacheValue value = cache.getIfPresent(key);
        if (value == null) {
            missedKeys.put(key, true);
            return null;
        }
        value.hitCount.incrementAndGet();
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

    @Override
    public List<CacheKeyInfo> getCacheKeyList(boolean includeMissed) {
        List<CacheKeyInfo> keyInfoList = new ArrayList<>();
        long now = System.currentTimeMillis();
        
        cache.asMap().forEach((key, value) -> {
            CacheKeyInfo info = new CacheKeyInfo();
            info.setKey(key);
            info.setCreatedAt(value.getCreatedAt());
            long cachedDurationSeconds = (now - value.getCreatedAt()) / 1000;
            long remainingTtl = value.getTimeout() > 0 ? value.getTimeout() - cachedDurationSeconds : 0;
            info.setTtlSeconds(Math.max(0, remainingTtl));
            info.setCachedDurationSeconds(cachedDurationSeconds);
            info.setHitCount(value.getHitCount());
            info.setMemorySizeBytes(estimateMemorySize(key, value));
            keyInfoList.add(info);
        });
        
        if (includeMissed) {
            missedKeys.asMap().forEach((key, value) -> {
                if (!cache.asMap().containsKey(key)) {
                    CacheKeyInfo info = new CacheKeyInfo();
                    info.setKey(key);
                    info.setCreatedAt(0);
                    info.setTtlSeconds(0);
                    info.setCachedDurationSeconds(0);
                    info.setHitCount(0);
                    info.setMemorySizeBytes(0);
                    keyInfoList.add(info);
                }
            });
        }
        
        return keyInfoList;
    }

    private long estimateMemorySize(String key, CacheValue value) {
        long size = 0;
        
        size += estimateStringSize(key);
        
        if (value.getValue() != null) {
            size += estimateStringSize(value.getValue());
        }
        
        size += 48;
        
        if (value.counter != null) {
            size += 24;
        }
        
        return size;
    }
    
    private long estimateStringSize(String str) {
        if (str == null) {
            return 0;
        }
        return 40 + (long) str.length() * 2L;
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
        private final long createdAt;
        private final AtomicLong hitCount = new AtomicLong(0);

        public CacheValue(String value, long timeout) {
            this.value = value;
            this.timeout = timeout;
            this.createdAt = System.currentTimeMillis();
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

        public long getCreatedAt() {
            return createdAt;
        }

        public long getHitCount() {
            return hitCount.get();
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
