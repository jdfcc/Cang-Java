package com.Cang.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.websocket.Session;
import java.time.Duration;
import java.util.Objects;

/**
 * @author Jdfcc
 * @Description 为RedisTemplate设置序列化器
 * @DateTime 2023/5/15 19:11
 */

@Configuration
@EnableCaching
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
//        创建RedisTemplate对象
        RedisTemplate<String, Object> template = new RedisTemplate<>();

//        设置连接工厂
        template.setConnectionFactory(connectionFactory);

//        创建json序列化工具
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();

//        设置key序列化
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
//        设置value序列化
        template.setValueSerializer(jsonRedisSerializer);
        template.setHashValueSerializer(jsonRedisSerializer);
//        返回
        return template;
    }

    /**
     * 修改 Cacheable 默认序列化方式 使用Redis配置的序列化
     * 解决 @Cacheable 序列化失败 而 RedisUtil可以成功 问题
     *
     * @param redisTemplate RedisTemplate
     * @return RedisCacheManager
     */
//    @Bean
//    public RedisCacheManager redisCacheManager(RedisTemplate<String, Object> redisTemplate) {
//        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(Objects.requireNonNull(redisTemplate.getConnectionFactory()));
//        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
//                // 设置默认的超时时间为2小时
//                .entryTtl(Duration.ofHours(2))
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisTemplate.getValueSerializer()))
//                // 设置默认的缓存前缀
//                .prefixCacheNameWith("CACHE_");
//        return new RedisCacheManager(redisCacheWriter, redisCacheConfiguration);
//    }
}