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
            log.error("解锁失败，key:{} 不存在锁", key);
            return;
        }

        if (!lock.isHeldByCurrentThread()) {
            log.error("当前线程未持有锁，禁止解锁，key:{}", key);
            return;
        }

        try {
            lock.unlock();
        } finally {
            // 修复：原子安全清理，彻底解决内存泄漏
            lockMap.computeIfPresent(key, (k, currentLock) -> {
                if (!currentLock.isLocked()) {
                    return null; // 返回null代表删除key
                }
                return currentLock;
            });
        }
    }

    @Override
    public boolean tryLock(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key 不能为空");
        }
        ReentrantLock lock = lockMap.computeIfAbsent(key, k -> new ReentrantLock());
        return lock.tryLock();
    }

    @Override
    public boolean tryLock(String key, long time, TimeUnit unit) {
        if (key == null) {
            throw new IllegalArgumentException("key 不能为空");
        }
        ReentrantLock lock = lockMap.computeIfAbsent(key, k -> new ReentrantLock());
        try {
            return lock.tryLock(time, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("获取锁被中断，key:{}", key);
            return false;
        }
    }
}