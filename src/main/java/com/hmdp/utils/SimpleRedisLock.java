package com.hmdp.utils;

import cn.hutool.core.util.BooleanUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

public class SimpleRedisLock implements ILock {

    private StringRedisTemplate redisTemplate;
    private String key;
    private static final String key_prefix = "hmdp:voucherOrder:lock:";

    public SimpleRedisLock(StringRedisTemplate redisTemplate, String key) {
        this.redisTemplate = redisTemplate;
        this.key = key;
    }

    @Override
    public Boolean tryLock(Long timeSec) {
        long id = Thread.currentThread().getId();
        boolean flag = redisTemplate.opsForValue().
                setIfAbsent(key_prefix + key, String.valueOf(id),timeSec, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    @Override
    public void unLock() {
        redisTemplate.delete(key_prefix+key);

    }
}
