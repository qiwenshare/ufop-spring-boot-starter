package com.qiwenshare.ufop.lock.impl;

import com.qiwenshare.ufop.lock.LockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class LockServiceJDKImpl implements LockService {
    private final Map<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();
    @Override
    public void lock(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key 不能为空");
        }

        // 获取或创建一个ReentrantLock对象
        ReentrantLock lock = lockMap.computeIfAbsent(key, k -> new ReentrantLock());
        // 获取锁
        lock.lock();
    }

    @Override
    public void unlock(String key) {

        if (key == null) {
            throw new IllegalArgumentException("key 不能为空");
        }

        // 从Map中获取锁对象
        ReentrantLock lock = lockMap.get(key);
        // 获取不到报错
        if (lock == null) {
            throw new IllegalArgumentException("key " + key + "尚未加锁");
        }
        // 其他线程非法持有不允许释放
        if (!lock.isHeldByCurrentThread()) {
            log.error("当前线程尚未持有，key:" + key + "的锁，不允许释放");
            return;
        }

        lock.unlock();
    }

    @Override
    public boolean tryLock(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key 不能为空");
        }

        // 从Map中获取锁对象
        ReentrantLock lock = lockMap.get(key);
        // 获取不到报错
        if (lock == null) {
            throw new IllegalArgumentException("key " + key + "尚未加锁");
        }

        return lock.tryLock();
    }

    @Override
    public boolean tryLock(String key, long time, TimeUnit unit) {
        if (key == null) {
            throw new IllegalArgumentException("key 不能为空");
        }

        // 从Map中获取锁对象
        ReentrantLock lock = lockMap.get(key);
        // 获取不到报错
        if (lock == null) {
            throw new IllegalArgumentException("key " + key + "尚未加锁");
        }

        try {
            return lock.tryLock( time, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
