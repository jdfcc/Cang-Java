package com.Cang.utils;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.BooleanUtil;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.Cang.constants.RedisConstants.KEY_PREFIX;

/**
 * @author Jdfcc
 */
public class SimpleRedisLock implements ILock {

    private StringRedisTemplate redisTemplate;
    private String key;

    private static final String id_prefix = UUID.randomUUID().toString(true) + "-";

    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;

    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("unlock.lua"));
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

    public SimpleRedisLock(StringRedisTemplate redisTemplate, String key) {
        this.redisTemplate = redisTemplate;
        this.key = key;
    }

    @Override
    public Boolean tryLock(Long timeSec) {
        String id = id_prefix + Thread.currentThread().getId();
        boolean flag = redisTemplate.opsForValue().
                setIfAbsent(KEY_PREFIX + key, id, timeSec, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    @Override
    public void unLock() {
        String finalKey = KEY_PREFIX +key;
        String name = redisTemplate.opsForValue().get(finalKey);
        List<String> KEYS = new ArrayList();
        KEYS.add(name);
        String value=id_prefix+Thread.currentThread().getId();
        redisTemplate.execute(UNLOCK_SCRIPT, KEYS, value);
    }
}
