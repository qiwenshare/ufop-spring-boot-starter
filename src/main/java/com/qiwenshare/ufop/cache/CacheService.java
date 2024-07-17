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
     * 将值放入缓存并设置时间-秒
     * @param key 键
     * @param value 值
     * @param time 时间（单位：秒），如果值为负数，则永久
     */
    void set(String key, String value, long time);


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
}
