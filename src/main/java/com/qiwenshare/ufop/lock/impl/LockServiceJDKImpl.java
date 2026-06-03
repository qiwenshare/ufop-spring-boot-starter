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

        // 修复内存泄漏：当锁不再被任何线程持有时，从Map中移除
        if (!lock.isLocked()) {
            lockMap.remove(key, lock);
            log.debug("释放锁并清理Map中的锁对象，key: {}", key);
        }
    }

    @Override
    public boolean tryLock(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key 不能为空");
        }

        // 获取或创建一个ReentrantLock对象
        ReentrantLock lock = lockMap.computeIfAbsent(key, k -> new ReentrantLock());
        // 尝试获取锁
        boolean acquired = lock.tryLock();

        // 如果获取锁失败，且锁没有被任何线程持有，可以考虑清理（但通常保留也没问题）
        // 注意：这里不主动清理，因为锁可能很快就会被使用
        // 清理工作主要放在unlock方法中

        return acquired;
    }

    @Override
    public boolean tryLock(String key, long time, TimeUnit unit) {
        if (key == null) {
            throw new IllegalArgumentException("key 不能为空");
        }

        // 获取或创建一个ReentrantLock对象
        ReentrantLock lock = lockMap.computeIfAbsent(key, k -> new ReentrantLock());

        try {
            boolean acquired = lock.tryLock(time, unit);

            // 如果获取锁失败，且锁没有被任何线程持有，可以考虑清理
            // 注意：这里不主动清理，因为锁可能很快就会被使用
            // 清理工作主要放在unlock方法中

            return acquired;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("获取锁被中断，key: {}", key);
            return false;
        }
    }
}