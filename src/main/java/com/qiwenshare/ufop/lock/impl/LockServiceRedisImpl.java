package com.qiwenshare.ufop.lock.impl;

import com.qiwenshare.ufop.lock.LockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于Redis实现的分布式锁服务
 * 特点：
 * 1. 支持阻塞和非阻塞获取锁
 * 2. 支持锁超时自动释放
 * 3. 保证解锁操作的原子性
 * 4. 适合分布式环境使用
 */
@Service
@Slf4j
public class LockServiceRedisImpl implements LockService {

    // 默认获取锁的重试间隔(毫秒)
    private static final int DEFAULT_ACQUIRE_RESOLUTION_MILLIS = 100;
    // 默认锁过期时间(秒)
    private static final long DEFAULT_LOCK_EXPIRE_SECONDS = TimeUnit.MINUTES.toSeconds(5);
    // 无超时标志
    private static final long NO_TIMEOUT = -1;
    // Redis键前缀，避免与其他业务key冲突
    private static final String LOCK_PREFIX = "lock:";

    /**
     * 解锁Lua脚本
     * 保证判断锁归属和删除锁的原子性
     * 逻辑：
     * 1. 如果锁存在且值匹配，则删除锁
     * 2. 如果锁不存在，也返回成功
     * 3. 其他情况返回失败
     */
    private static final String UNLOCK_LUA =
            "local lockKey = KEYS[1]\n" +
                    "local lockValue = ARGV[1]\n" +
                    "local currentValue = redis.call('get', lockKey)\n" +
                    "if currentValue == lockValue then\n" +
                    "    redis.call('del', lockKey)\n" +
                    "    return 1\n" +
                    "elseif currentValue == false then\n" +
                    "    return 1\n" +
                    "else\n" +
                    "    return 0\n" +
                    "end";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取锁（阻塞式）
     * @param key 锁的业务键
     */
    @Override
    public void lock(String key) {
        acquireLockWithException(key, DEFAULT_LOCK_EXPIRE_SECONDS, NO_TIMEOUT);
    }

    /**
     * 释放锁
     * @param key 锁的业务键
     */
    @Override
    public void unlock(String key) {
        try {
            release(key);
        } catch (Exception e) {
            throw new LockOperationException("release lock exception", e);
        }
    }

    /**
     * 尝试获取锁（非阻塞式）
     * @param key 锁的业务键
     * @return 是否获取成功
     */
    @Override
    public boolean tryLock(String key) {
        return acquireLockWithException(key, DEFAULT_LOCK_EXPIRE_SECONDS, NO_TIMEOUT);
    }

    /**
     * 尝试获取锁（带超时）
     * @param key 锁的业务键
     * @param time 超时时间
     * @param unit 时间单位
     * @return 是否获取成功
     */
    @Override
    public boolean tryLock(String key, long time, TimeUnit unit) {
        return acquireLockWithException(key, DEFAULT_LOCK_EXPIRE_SECONDS, unit.toSeconds(time));
    }

    /**
     * 封装获取锁的异常处理
     * @param key 锁的业务键
     * @param expire 锁过期时间(秒)
     * @param waitTime 等待超时时间(秒)
     * @return 是否获取成功
     */
    private boolean acquireLockWithException(String key, long expire, long waitTime) {
        try {
            return acquireLock(key, expire, waitTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockOperationException("acquire lock interrupted", e);
        } catch (Exception e) {
            throw new LockOperationException("acquire lock exception", e);
        }
    }

    /**
     * 核心获取锁逻辑
     * @param key 锁的业务键
     * @param expire 锁过期时间(秒)
     * @param waitTime 等待超时时间(秒)
     * @return 是否获取成功
     * @throws InterruptedException 线程中断异常
     */
    private boolean acquireLock(String key, long expire, long waitTime) throws InterruptedException {
        // 构造完整的Redis键
        String fullKey = LOCK_PREFIX + key;
        // 生成唯一锁标识
        String lockId = UUID.randomUUID().toString();
        // 重试计数器
        AtomicInteger retryCount = new AtomicInteger(0);

        // 计算获取锁的绝对超时时间
        long acquireTimeout = waitTime == NO_TIMEOUT ?
                Long.MAX_VALUE :
                System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(waitTime);

        // 在超时时间内循环尝试获取锁
        while (System.currentTimeMillis() < acquireTimeout) {
            // 尝试获取Redis锁
            if (tryRedisLock(fullKey, lockId, expire)) {
                log.debug("Acquired lock {} successfully", key);
                return true;
            }

            // 根据是否设置超时决定等待时间
            if (waitTime == NO_TIMEOUT) {
                TimeUnit.MILLISECONDS.sleep(DEFAULT_ACQUIRE_RESOLUTION_MILLIS);
            } else {
                long remaining = acquireTimeout - System.currentTimeMillis();
                if (remaining <= 0) break;
                // 取剩余时间和默认间隔的较小值
                TimeUnit.MILLISECONDS.sleep(Math.min(DEFAULT_ACQUIRE_RESOLUTION_MILLIS, remaining));
            }

            // 每10次重试打印一次日志
            if (retryCount.incrementAndGet() % 10 == 0) {
                log.debug("Still waiting for lock {}, retry count: {}", key, retryCount.get());
            }
        }

        log.info("Failed to acquire lock {} after {} retries", key, retryCount.get());
        return false;
    }

    /**
     * 尝试获取Redis锁
     * @param fullKey 完整的Redis键
     * @param lockId 锁的唯一标识
     * @param expireSeconds 过期时间(秒)
     * @return 是否获取成功
     */
    private boolean tryRedisLock(String fullKey, String lockId, long expireSeconds) {
        try {
            // 使用SET命令的NX选项实现原子性获取锁
            RedisCallback<Boolean> callback = connection ->
                    connection.set(
                            fullKey.getBytes(StandardCharsets.UTF_8),
                            lockId.getBytes(StandardCharsets.UTF_8),
                            Expiration.seconds(expireSeconds),
                            RedisStringCommands.SetOption.SET_IF_ABSENT
                    );
            return Boolean.TRUE.equals(stringRedisTemplate.execute(callback));
        } catch (Exception e) {
            log.error("Redis lock error for key: {}", fullKey, e);
            return false;
        }
    }

    /**
     * 释放锁
     * @param key 锁的业务键
     */
    private void release(String key) {
        String fullKey = LOCK_PREFIX + key;
        // 获取当前锁的值
        String lockId = getCurrentLockId(fullKey);

        if (lockId == null) {
            log.debug("No active lock found for key: {}", key);
            return;
        }

        // 执行解锁脚本
        executeUnlockScript(fullKey, lockId);
    }

    /**
     * 获取当前锁的值
     * @param fullKey 完整的Redis键
     * @return 锁的值，获取失败返回null
     */
    private String getCurrentLockId(String fullKey) {
        try {
            return stringRedisTemplate.opsForValue().get(fullKey);
        } catch (Exception e) {
            log.error("Failed to get lock value for key: {}", fullKey, e);
            return null;
        }
    }

    /**
     * 执行解锁Lua脚本
     * @param fullKey 完整的Redis键
     * @param lockId 锁的唯一标识
     */
    private void executeUnlockScript(String fullKey, String lockId) {
        try {
            RedisCallback<Long> callback = connection ->
                    connection.eval(
                            UNLOCK_LUA.getBytes(StandardCharsets.UTF_8),
                            ReturnType.INTEGER,
                            1,
                            fullKey.getBytes(StandardCharsets.UTF_8),
                            lockId.getBytes(StandardCharsets.UTF_8)
                    );

            // 执行脚本并处理结果
            Long result = stringRedisTemplate.execute(callback);
            if (result != null && result == 1) {
                log.debug("Released lock {} successfully", fullKey.substring(LOCK_PREFIX.length()));
            } else {
                log.warn("Failed to release lock {}, possibly already expired or released",
                        fullKey.substring(LOCK_PREFIX.length()));
            }
        } catch (Exception e) {
            log.error("Error while releasing lock: {}", fullKey, e);
        }
    }

    /**
     * 锁操作异常
     */
    private static class LockOperationException extends RuntimeException {
        LockOperationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}