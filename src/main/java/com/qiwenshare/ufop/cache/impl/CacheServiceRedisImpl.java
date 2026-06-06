package com.qiwenshare.ufop.cache.impl;


import com.qiwenshare.ufop.cache.CacheKeyInfo;
import com.qiwenshare.ufop.cache.CacheService;
import com.qiwenshare.ufop.cache.CacheStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class CacheServiceRedisImpl implements CacheService {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    /**
     * 将值放入缓存
     * @param key 键
     * @param value 值
     */
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 获取对象
     * @param key 键
     * @return 返回值
     */
    public String getObject(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 将值放入缓存并设置时间-秒
     * @param key 键
     * @param value 值
     * @param time 时间（单位：秒），如果值为负数，则永久
     */
    public void set(String key, String value, long time) {
        if (time > 0) {
            stringRedisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
        } else {
            stringRedisTemplate.opsForValue().set(key, value);
        }
    }


    public boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 删除key
     * @param key key
     */
    public void deleteKey(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 获取自增长值
     * @param key 键
     * @return 返回增长之后的值
     */
    public Long getIncr(String key) {
        return stringRedisTemplate.opsForValue().increment(key, 1);
    }

    @Override
    public CacheStats getCacheStats() {
        CacheStats stats = new CacheStats();
        
        try {
            Map<String, String> info = stringRedisTemplate.execute((RedisCallback<Map<String, String>>) connection -> {
                java.util.Properties props = connection.info("stats");
                Map<String, String> result = new java.util.HashMap<>();
                for (String key : props.stringPropertyNames()) {
                    result.put(key, props.getProperty(key));
                }
                return result;
            });
            
            if (info != null) {
                stats.setHitCount(parseLong(info.get("keyspace_hits"), 0));
                stats.setMissCount(parseLong(info.get("keyspace_misses"), 0));
                stats.setEvictionCount(parseLong(info.get("evicted_keys"), 0));
            }
            
            Long dbSize = stringRedisTemplate.execute((RedisCallback<Long>) connection -> connection.dbSize());
            stats.setEstimatedSize(dbSize != null ? dbSize : 0);
            
            stats.setLoadSuccessCount(0);
            stats.setLoadFailureCount(0);
            stats.setTotalLoadTime(0);
            
        } catch (Exception e) {
            log.error("获取Redis统计信息失败", e);
            stats.setEstimatedSize(0);
            stats.setHitCount(0);
            stats.setMissCount(0);
            stats.setLoadSuccessCount(0);
            stats.setLoadFailureCount(0);
            stats.setTotalLoadTime(0);
            stats.setEvictionCount(0);
        }
        
        return stats;
    }

    @Override
    public List<CacheKeyInfo> getCacheKeyList(boolean includeMissed) {
        List<CacheKeyInfo> keyInfoList = new ArrayList<>();
        long now = System.currentTimeMillis();
        
        try {
            Cursor<byte[]> cursor = stringRedisTemplate.executeWithStickyConnection((RedisCallback<Cursor<byte[]>>) connection -> 
                connection.scan(ScanOptions.scanOptions().match("*").count(1000).build())
            );
            
            if (cursor != null) {
                while (cursor.hasNext()) {
                    byte[] keyBytes = cursor.next();
                    String key = new String(keyBytes);
                    
                    Long ttl = stringRedisTemplate.getExpire(key);
                    Long memorySize = getMemoryUsage(key);
                    
                    CacheKeyInfo info = new CacheKeyInfo();
                    info.setKey(key);
                    info.setCreatedAt(now - (ttl != null && ttl > 0 ? ttl * 1000 : 0));
                    info.setTtlSeconds(ttl != null ? ttl : 0);
                    info.setCachedDurationSeconds(ttl != null && ttl > 0 ? 0 : 0);
                    info.setHitCount(0);
                    info.setMemorySizeBytes(memorySize != null ? memorySize : 0);
                    
                    keyInfoList.add(info);
                }
                cursor.close();
            }
        } catch (Exception e) {
            log.error("获取Redis key列表失败", e);
        }
        
        return keyInfoList;
    }
    
    private Long getMemoryUsage(String key) {
        try {
            return stringRedisTemplate.execute((RedisCallback<Long>) connection -> {
                try {
                    Object result = connection.execute("MEMORY", "USAGE".getBytes(), key.getBytes());
                    if (result instanceof Long) {
                        return (Long) result;
                    }
                    if (result instanceof byte[]) {
                        return Long.parseLong(new String((byte[]) result));
                    }
                    return 0L;
                } catch (Exception e) {
                    return 0L;
                }
            });
        } catch (Exception e) {
            return 0L;
        }
    }

    private long parseLong(String value, long defaultValue) {
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
