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

        // 原子性地获取或创建锁
        ReentrantLock lock = lockMap.computeIfAbsent(key, k -> new ReentrantLock());
        lock.lock();
    }

    @Override
    public void unlock(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key 不能为空");
        }

        ReentrantLock lock = lockMap.get(key);
        if (lock == null) {
            throw new IllegalStateException("key " + key + " 没有对应的锁");
        }

        if (!lock.isHeldByCurrentThread()) {
            throw new IllegalStateException("当前线程不持有 key: " + key + " 的锁");
        }

        lock.unlock();

        // 如果锁已经完全释放且没有线程在等待，从map中移除
        if (lock.getHoldCount() == 0 && !lock.hasQueuedThreads()) {
            lockMap.remove(key, lock);
        }
    }

    @Override
    public boolean tryLock(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key 不能为空");
        }

        // 原子性地获取或创建锁并尝试获取
        ReentrantLock lock = lockMap.computeIfAbsent(key, k -> new ReentrantLock());
        boolean acquired = lock.tryLock();

        // 如果获取失败且锁未被任何线程持有，尝试清理
        if (!acquired && lock.getHoldCount() == 0 && !lock.hasQueuedThreads()) {
            lockMap.remove(key, lock);
        }
        return acquired;
    }

    @Override
    public boolean tryLock(String key, long time, TimeUnit unit) {
        if (key == null) {
            throw new IllegalArgumentException("key 不能为空");
        }
        if (time < 0) {
            throw new IllegalArgumentException("等待时间不能为负数");
        }

        ReentrantLock lock = lockMap.computeIfAbsent(key, k -> new ReentrantLock());
        try {
            boolean acquired = lock.tryLock(time, unit);

            // 如果获取失败且锁未被任何线程持有，尝试清理
            if (!acquired && lock.getHoldCount() == 0 && !lock.hasQueuedThreads()) {
                lockMap.remove(key, lock);
            }
            return acquired;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("获取锁被中断", e);
        }
    }
}