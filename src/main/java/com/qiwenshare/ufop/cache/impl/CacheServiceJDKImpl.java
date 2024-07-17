package com.qiwenshare.ufop.cache.impl;


import com.qiwenshare.ufop.cache.CacheService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class CacheServiceJDKImpl implements CacheService {



//    private static Map<String, Object> cacheMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Value<String, Object>> cache= new ConcurrentHashMap<>();
    private int capacity=500000;

    private final Object lock = new Object();
    @Override
    public void set(String key, String val) {
        eliminate();
        Value<String, Object> value = new Value<>(key, val, 0, null);
        cache.put(key, value);
    }

    @Override
    public String getObject(String key) {
        if (expire(key)) {
            return null;
        }
        Value<String, Object> val = cache.get(key);
        if (val == null) return null;
        val.count.incrementAndGet();
        return String.valueOf(val.val);
    }

    @Override
    public void set(String key, String val, long time) {
        eliminate();
        if (time > 0) {
            Value<String, Object> value = new Value<>(key, val, time, TimeUnit.SECONDS);
            cache.put(key, value);
        } else {
            set(key, val);
        }
    }

    @Override
    public boolean hasKey(String key) {
        return cache.get(key) != null;
    }

    @Override
    public void deleteKey(String key) {
        cache.remove(key);
    }

    @Override
    public Long getIncr(String key) {
        String res = getObject(key);
        Long val = Long.valueOf(res);
        set(key, String.valueOf(val + 1));
        return val + 1;
    }

    public boolean expire(String key) {
        Value<String, Object> value = cache.get(key);
        if (value == null || value.timeout == 0) {
            return false;
        }
        boolean expire = (System.currentTimeMillis() - value.timestamp) > TimeUnit.MILLISECONDS.convert(value.timeout, value.unit);
        if (expire) {
            cache.remove(key, value);
        }
        return expire;
    }

    /**
     * 淘汰策略
     * 1、如果初始的容量小于100
     */
    private void eliminate() {
        if (cache.size() >= capacity) {//如果当前容器中的数据量超过了规定的容量则进行数据淘汰

            synchronized (lock) {

                Collection<Value<String, Object>> values = cache.values();
                long expire = values.stream().filter(Value::expire).count();

                if ((expire > capacity / 2)) {//如果容器中的过期数据占比超过1/2则创建新容器并复制数据
                    cache = new ConcurrentHashMap<>(capacity);
                    values.stream().filter(e -> !e.expire()).forEach(data -> {
                        //数据复制
                        cache.put(data.key, data);
                    });
                } else {//否则创建新的容器将原容器中未过期的数据按照访问量降序排列  取列表前1/5的数据并复制到新的容器
                    cache = new ConcurrentHashMap<>(capacity);

                    cache.values().stream()
                            .filter(e -> !e.expire())
                            .sorted((o1, o2) -> o2.count.get() - o1.count.get())
                            .limit(size() / 5).forEach(data -> {
                                //数据复制
                                cache.put(data.key, data);

                            });

                }

            }

        }
    }

    public int size() {
        return cache.size();
    }

    /**
     * 缓存容器中真正存储的 value数据
     *
     * @param <K>
     * @param <V>
     */
    private static class Value<K, V> {
        private K key;//用户传入缓存的key
        private V val; //用户缓存传入缓存的value
        private long timestamp;//时间戳
        private long timeout;//key的过期时间
        private TimeUnit unit;//时间单位
        private AtomicInteger count;//保存该条数据的访问量

        public Value(K key, V val, long timeout, TimeUnit unit) {
            this.key = key;
            this.val = val;
            this.timestamp = System.currentTimeMillis();
            this.timeout = timeout;
            this.unit = unit;
            this.count = new AtomicInteger();
        }

        /**
         * 是否过期
         *
         * @return
         */
        public boolean expire() {
            return (System.currentTimeMillis() - timestamp) > TimeUnit.MILLISECONDS.convert(timeout, unit);
        }

        public K getKey() {
            return key;
        }

        public V getVal() {
            return val;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public long getTimeout() {
            return timeout;
        }

        public TimeUnit getUnit() {
            return unit;
        }

        public AtomicInteger getCount() {
            return count;
        }

        @Override
        public String toString() {
            return "Value{" +
                    "key=" + key +
                    ", val=" + val +
                    ", timestamp=" + timestamp +
                    ", timeout=" + timeout +
                    ", unit=" + unit +
                    ", count=" + count +
                    '}';
        }
    }
}
