package com.qiwenshare.ufop.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisUtil {

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

}