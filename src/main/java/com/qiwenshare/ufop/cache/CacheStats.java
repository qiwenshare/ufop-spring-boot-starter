package com.qiwenshare.ufop.cache;

public class CacheStats {
    
    private long hitCount;
    private long missCount;
    private long loadSuccessCount;
    private long loadFailureCount;
    private long totalLoadTime;
    private long evictionCount;
    private long estimatedSize;

    public long getHitCount() {
        return hitCount;
    }

    public void setHitCount(long hitCount) {
        this.hitCount = hitCount;
    }

    public long getMissCount() {
        return missCount;
    }

    public void setMissCount(long missCount) {
        this.missCount = missCount;
    }

    public long getLoadSuccessCount() {
        return loadSuccessCount;
    }

    public void setLoadSuccessCount(long loadSuccessCount) {
        this.loadSuccessCount = loadSuccessCount;
    }

    public long getLoadFailureCount() {
        return loadFailureCount;
    }

    public void setLoadFailureCount(long loadFailureCount) {
        this.loadFailureCount = loadFailureCount;
    }

    public long getTotalLoadTime() {
        return totalLoadTime;
    }

    public void setTotalLoadTime(long totalLoadTime) {
        this.totalLoadTime = totalLoadTime;
    }

    public long getEvictionCount() {
        return evictionCount;
    }

    public void setEvictionCount(long evictionCount) {
        this.evictionCount = evictionCount;
    }

    public long getEstimatedSize() {
        return estimatedSize;
    }

    public void setEstimatedSize(long estimatedSize) {
        this.estimatedSize = estimatedSize;
    }

    public double getHitRate() {
        long total = hitCount + missCount;
        return total == 0 ? 0.0 : (double) hitCount / total;
    }

    public double getLoadSuccessRate() {
        long total = loadSuccessCount + loadFailureCount;
        return total == 0 ? 0.0 : (double) loadSuccessCount / total;
    }
}
