package com.hmdp.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static com.hmdp.utils.RedisConstants.REDIS_INCREASE_KEY;

/**
 * @author jdfcc
 * @description id生成器
 */
@Component
public class RedisIdWorker {

    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final long BEGIN_TIMESTAMP = 1640995200L;
    private static final String TIME_PATTERN = "yyyy:MM:dd";
    private static final int COUNT_BITS = 32;

    public Long nextId(String keyPrefix) {
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timeStamp = nowSecond - BEGIN_TIMESTAMP;

        String date = now.format(DateTimeFormatter.ofPattern(TIME_PATTERN));
        Long increment = redisTemplate.opsForValue().increment(REDIS_INCREASE_KEY + keyPrefix + ":" + date);

        return timeStamp << COUNT_BITS | increment;
    }

}
