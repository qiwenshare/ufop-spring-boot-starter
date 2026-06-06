package com.qiwenshare.ufop.cache;


public interface CacheService {

    /**
     * 将值放入缓存
     * @param key 键
     * @param value 值
     */
    void set(String key, String value);

    /**
     * 获取对象
     * @param key 键
     * @return 返回值
     */
    String getObject(String key);

    /**
     * 将值放入缓存并设置过期时间
     * @param key 键
     * @param value 值
     * @param timeoutSeconds 过期时间（单位：秒），如果值小于等于0，则永久有效
     */
    void set(String key, String value, long timeoutSeconds);


    boolean hasKey(String key);

    /**
     * 删除key
     * @param key key
     */
    void deleteKey(String key);

    /**
     * 获取自增长值
     * @param key 键
     * @return 返回增长之后的值
     */
    Long getIncr(String key);

    /**
     * 获取缓存统计信息
     * @return 缓存统计信息，包含缓存大小、命中率等
     */
    CacheStats getCacheStats();

    /**
     * 获取所有缓存key的列表及相关信息
     * @return 缓存key信息列表
     */
    java.util.List<CacheKeyInfo> getCacheKeyList();
}
