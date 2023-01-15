package com.hmdp.utils;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hmdp.entity.RedisData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.hmdp.utils.RedisConstants.*;

@Component

public class CacheClient {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedisData redisData;

    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    /**
     * 将key,data存进redis中并设置过期时间
     *
     * @param key
     * @param data
     * @param time
     * @param unit
     */
    public void set(String key, Object data, Long time, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(data), time, unit);
    }

    /**
     * 查询名为key+id的对象储存进redis并返回，利用存储空值解决缓存穿透问题
     *
     * @param key
     * @param id
     * @param type
     * @param dbFallback
     * @param saveTime
     * @param unit
     * @param <R>
     * @param <ID>
     * @return
     */
    public <R, ID> R queryWithCacheThrough(
            String key, ID id, Class<R> type, Function<ID, R> dbFallback, Long saveTime, TimeUnit unit) {
        String queryId = key + CACHE_ENTITY_KEY+String.valueOf(id);
        String json = redisTemplate.opsForValue().get(queryId);
        if (StrUtil.isNotBlank(json))
            return JSONUtil.toBean(json, type);
        //            处理缓存穿透
        if (json != null)//为处理缓存穿透所存的空字符串
            return null;
        R r = dbFallback.apply(id);
        if (r == null) {
            redisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, unit);
            return null;
        }
        this.set(queryId, r, saveTime, unit);
        return r;
    }

    /**
     * 将data以key命名后以RedisData类封装后存进redis中并设置逻辑过期时间
     *
     * @param key
     * @param data
     * @param expireTime
     * @param unit
     */
    public void setWithLogicExpire(String key, Object data, Long expireTime, TimeUnit unit) {
        redisData.setData(data);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(expireTime)));
        redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    /**
     * 查询名为key+id的对象储存进redis并返回，利用互斥锁解决缓存击穿问题
     *
     * @param keyPrefix
     * @param id
     * @param type
     * @param dbFallback
     * @param saveTime
     * @param unit
     * @param <R>
     * @param <ID>
     * @return
     */
    public <R, ID> R queryWithLogicalExpire(
            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long saveTime, TimeUnit unit) {
        String queryKey = keyPrefix + String.valueOf(id);
        String json = redisTemplate.opsForValue().get(queryKey);
        if (StrUtil.isBlank(json))
            return null;
//      命中,将json反序列化成对象
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        JSONObject data = (JSONObject) redisData.getData();
        R r = JSONUtil.toBean(data, type);
        LocalDateTime expireTime = redisData.getExpireTime();
//        判断是否过期
        if (expireTime.isAfter(LocalDateTime.now())) {
//          未过期，直接返回
//            log.info("缓存未过期");
            return r;
        }

//        已过期，需要重建缓存
        String lockKey = LOCK_KEY + id;
//        log.info("缓存已过期");
        if (getLock(lockKey))
//            log.info("获取互斥锁成功");
//            获取互斥锁成功，开启另外一个进程开始数据重建任务
        CACHE_REBUILD_EXECUTOR.submit(() -> {
            try {
                String newJson = redisTemplate.opsForValue().get(queryKey);
//            检查数据重建是否已由其他线程完成，即缓存逻辑过期时间是否已为未过期
                RedisData newRedisData = JSONUtil.toBean(newJson, RedisData.class);
                LocalDateTime newExpireTime = newRedisData.getExpireTime();
                if (newExpireTime.isAfter(LocalDateTime.now())) {
                    //      数据重建完成,逻辑过期时间未过期，将json反序列化成对象并返回
                    JSONObject newData = (JSONObject) newRedisData.getData();
                    R newR = JSONUtil.toBean(newData, type);
                    return newR;
                }
//            数据重建未完成，查询数据库
                R newR = dbFallback.apply(id);
                this.setWithLogicExpire(queryKey, newR, saveTime, unit);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                removeLock(lockKey);
            }
            return r;
        });
        return r;
    }

    /**
     * 获取互斥锁
     *
     * @param key
     * @return
     */
    public Boolean getLock(String key) {
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, LOCK_VALUE, LOCK_TTL, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    /**
     * 移除互斥锁
     *
     * @param key
     */
    public void removeLock(String key) {
        redisTemplate.delete(key);
    }

}
