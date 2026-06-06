package com.qiwenshare.ufop.cache;

public class CacheKeyInfo {
    
    private String key;
    private long createdAt;
    private long ttlSeconds;
    private long cachedDurationSeconds;
    private long hitCount;

    public CacheKeyInfo() {
    }

    public CacheKeyInfo(String key, long createdAt, long ttlSeconds) {
        this.key = key;
        this.createdAt = createdAt;
        this.ttlSeconds = ttlSeconds;
        this.cachedDurationSeconds = System.currentTimeMillis() / 1000 - createdAt / 1000;
        this.hitCount = 0;
    }

    public CacheKeyInfo(String key, long createdAt, long ttlSeconds, long hitCount) {
        this.key = key;
        this.createdAt = createdAt;
        this.ttlSeconds = ttlSeconds;
        this.cachedDurationSeconds = System.currentTimeMillis() / 1000 - createdAt / 1000;
        this.hitCount = hitCount;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getTtlSeconds() {
        return ttlSeconds;
    }

    public void setTtlSeconds(long ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }

    public long getCachedDurationSeconds() {
        return cachedDurationSeconds;
    }

    public void setCachedDurationSeconds(long cachedDurationSeconds) {
        this.cachedDurationSeconds = cachedDurationSeconds;
    }

    public long getHitCount() {
        return hitCount;
    }

    public void setHitCount(long hitCount) {
        this.hitCount = hitCount;
    }
    
    public String getCachedDurationFormatted() {
        long seconds = cachedDurationSeconds;
        if (seconds < 60) {
            return seconds + "秒";
        } else if (seconds < 3600) {
            return (seconds / 60) + "分" + (seconds % 60) + "秒";
        } else if (seconds < 86400) {
            return (seconds / 3600) + "小时" + ((seconds % 3600) / 60) + "分";
        } else {
            return (seconds / 86400) + "天" + ((seconds % 86400) / 3600) + "小时";
        }
    }
    
    public String getTtlFormatted() {
        if (ttlSeconds <= 0) {
            return "永不过期";
        }
        long seconds = ttlSeconds;
        if (seconds < 60) {
            return seconds + "秒";
        } else if (seconds < 3600) {
            return (seconds / 60) + "分" + (seconds % 60) + "秒";
        } else if (seconds < 86400) {
            return (seconds / 3600) + "小时" + ((seconds % 3600) / 60) + "分";
        } else {
            return (seconds / 86400) + "天" + ((seconds % 86400) / 3600) + "小时";
        }
    }
}
