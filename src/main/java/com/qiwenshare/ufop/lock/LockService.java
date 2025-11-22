package com.qiwenshare.ufop.lock;

import java.util.concurrent.TimeUnit;

public interface LockService {

    /**
     * 阻塞式获取锁
     * @param key 键
     */
    void lock(final String key);

    /**
     * 释放锁
     * @param key 键
     */
    void unlock(String key);

    /**
     * 非阻塞尝试获取锁
     * @param key 键
     * @return 返回是否获取成功
     */
    boolean tryLock(final String key);

    /**
     * 带超时尝试获取锁
     * @param key 键
     * @param time 获取锁等待时间
     * @param unit 时间单位
     * @return 返回是否获取成功
     */
    boolean tryLock(String key, long time, TimeUnit unit);
}
