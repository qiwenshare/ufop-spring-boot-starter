package com.qiwenshare.ufop.lock.impl;

import com.qiwenshare.ufop.lock.LockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于JDK ReentrantLock实现的本地锁服务
 * 解决了锁对象内存泄漏问题，提供完善的监控和清理机制
 *
 * @author QiwenShare
 */
@Service
@Slf4j
public class LockServiceJDKImpl implements LockService {

    private final Map<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    // 监控统计
    private final AtomicLong totalLockCount = new AtomicLong(0);

    @Override
    public void lock(String key) {
        validateKey(key);

        ReentrantLock lock = getOrCreateLock(key);
        lock.lock();
        log.debug("获取锁成功，key:{}, 线程:{}", key, Thread.currentThread().getName());
    }

    @Override
    public void unlock(String key) {
        validateKey(key);

        ReentrantLock lock = lockMap.get(key);
        if (lock == null) {
            log.warn("解锁失败，key:{} 对应的锁不存在", key);
            return;
        }

        if (!lock.isHeldByCurrentThread()) {
            log.warn("当前线程未持有锁，禁止解锁，key:{}, 线程:{}",
                    key, Thread.currentThread().getName());
            return;
        }

        try {
            lock.unlock();
            log.debug("释放锁成功，key:{}, 线程:{}", key, Thread.currentThread().getName());
        } catch (Exception e) {
            log.error("释放锁异常，key:{}, 线程:{}", key, Thread.currentThread().getName(), e);
            throw e;
        }
    }

    @Override
    public boolean tryLock(String key) {
        validateKey(key);

        ReentrantLock lock = getOrCreateLock(key);
        boolean acquired = lock.tryLock();

        if (acquired) {
            log.debug("tryLock获取锁成功，key:{}", key);
        } else {
            log.debug("tryLock获取锁失败，key:{}", key);
            // 获取失败，清理可能刚创建的未使用锁
            cleanupUnusedLock(key);
        }

        return acquired;
    }

    @Override
    public boolean tryLock(String key, long time, TimeUnit unit) {
        validateKey(key);

        ReentrantLock lock = getOrCreateLock(key);
        try {
            boolean acquired = lock.tryLock(time, unit);

            if (acquired) {
                log.debug("tryLock超时获取锁成功，key:{}, 超时:{}ms",
                        key, unit.toMillis(time));
            } else {
                log.debug("tryLock超时获取锁失败，key:{}, 超时:{}ms",
                        key, unit.toMillis(time));
                // 获取失败，清理可能刚创建的未使用锁
                cleanupUnusedLock(key);
            }

            return acquired;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("获取锁被中断，key:{}", key, e);
            // 中断情况下也尝试清理
            cleanupUnusedLock(key);
            return false;
        }
    }

    /**
     * 获取或创建锁对象
     */
    private ReentrantLock getOrCreateLock(String key) {
        ReentrantLock lock = lockMap.get(key);
        if (lock == null) {
            ReentrantLock newLock = new ReentrantLock();
            ReentrantLock existingLock = lockMap.putIfAbsent(key, newLock);
            if (existingLock == null) {
                totalLockCount.incrementAndGet();
                log.debug("创建新锁对象，key:{}, 当前锁总数:{}", key, lockMap.size());
                return newLock;
            }
            return existingLock;
        }
        return lock;
    }

   

    /**
     * 清理未使用的锁对象（在tryLock失败时调用）
     */
    private void cleanupUnusedLock(String key) {
        try {
            ReentrantLock lock = lockMap.get(key);
            if (lock != null && !lock.isLocked() && !lock.hasQueuedThreads()) {
                if (lockMap.remove(key, lock)) {
                    log.debug("清理获取失败的未使用锁，key:{}", key);
                }
            }
        } catch (Exception e) {
            log.error("清理未使用锁异常，key:{}", key, e);
        }
    }

    /**
     * 定时清理任务 - 定期清理长时间未使用的锁对象
     * 每5分钟执行一次
     */
    @Scheduled(fixedDelay = 300000, initialDelay = 60000)
    public void scheduledCleanup() {
        if (lockMap.isEmpty()) {
            return;
        }

        int cleanupCount = 0;
        int beforeSize = lockMap.size();

        try {
            // JDK 8 兼容的迭代清理方式
            for (Map.Entry<String, ReentrantLock> entry : lockMap.entrySet()) {
                String key = entry.getKey();
                ReentrantLock lock = entry.getValue();

                // 清理未被锁定且无等待线程的锁
                if (!lock.isLocked() && !lock.hasQueuedThreads()) {
                    if (lockMap.remove(key, lock)) {
                        cleanupCount++;
                        log.debug("定时清理锁对象，key:{}", key);
                    }
                }
            }

            if (cleanupCount > 0) {
                log.info("定时清理完成，清理锁对象数:{}, 清理前总数:{}, 清理后总数:{}",
                        cleanupCount, beforeSize, lockMap.size());
            } else {
                log.debug("定时清理完成，无需清理的锁对象，当前锁总数:{}", lockMap.size());
            }
        } catch (Exception e) {
            log.error("定时清理锁对象异常", e);
        }
    }

    /**
     * 校验key参数
     */
    private void validateKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("key 不能为空");
        }
    }

    /**
     * 获取当前锁信息（用于监控）
     * JDK 8 兼容版本
     */
    public Map<String, Object> getLockStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalLockCount", totalLockCount.get());
        stats.put("currentActiveLocks", lockMap.size());

        long lockedCount = 0;
        long queuedThreadsCount = 0;

        for (ReentrantLock lock : lockMap.values()) {
            if (lock.isLocked()) {
                lockedCount++;
            }
            if (lock.hasQueuedThreads()) {
                queuedThreadsCount++;
            }
        }

        stats.put("lockedCount", lockedCount);
        stats.put("queuedThreadsCount", queuedThreadsCount);

        return stats;
    }

    /**
     * 检查锁是否被当前线程持有
     */
    public boolean isLockedByCurrentThread(String key) {
        if (key == null) {
            return false;
        }
        ReentrantLock lock = lockMap.get(key);
        return lock != null && lock.isHeldByCurrentThread();
    }

    /**
     * 强制清理指定锁（谨慎使用）
     */
    public boolean forceCleanup(String key) {
        if (key == null) {
            return false;
        }

        ReentrantLock lock = lockMap.get(key);
        if (lock != null && !lock.isLocked() && !lock.hasQueuedThreads()) {
            if (lockMap.remove(key, lock)) {
                log.warn("强制清理锁对象，key:{}", key);
                return true;
            }
        }

        if (lock != null) {
            log.warn("强制清理失败，锁仍在使用，key:{}, isLocked:{}, hasQueuedThreads:{}",
                    key, lock.isLocked(), lock.hasQueuedThreads());
        }

        return false;
    }

    /**
     * 获取锁Map的大小（用于监控）
     */
    public int getLockMapSize() {
        return lockMap.size();
    }
}