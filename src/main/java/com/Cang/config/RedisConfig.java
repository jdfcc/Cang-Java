package com.Cang.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.websocket.Session;

/**
 * @author Jdfcc
 * @Description 为RedisTemplate设置序列化器
 * @DateTime 2023/5/15 19:11
 */

@Configuration
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






}
