package com.qiwenshare.ufop.lock;

import java.util.concurrent.TimeUnit;

public interface LockService {

    void lock(final String key);

    /**
     * 释放锁
     * @param key 键
     */
    void unlock(String key);

    /**
     * 尝试获取锁，没有获取到，返回false。否则 返回true
     * @param key 键
     * @return 返回是否获取成功
     */
    boolean tryLock(final String key);

    /**
     * 获取锁，指定时间内没有获取到，返回false。否则 返回true
     * @param key 键
     * @param time 获取锁等待时间
     * @param unit 时间单位
     * @return 返回是否获取成功
     */
    boolean tryLock(String key, long time, TimeUnit unit);
}
