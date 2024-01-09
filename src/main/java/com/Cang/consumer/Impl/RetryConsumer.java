package com.Cang.consumer.Impl;

import com.Cang.entity.MessageQueueEntity;
import com.Cang.enums.BusinessType;
import com.Cang.consumer.CommonQueueConsumer;
import com.Cang.template.MyRedisTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.Cang.constants.RabbitMqConstants.*;
import static com.Cang.constants.RedisConstants.DELETE_COUNT;
import static com.Cang.constants.RedisConstants.MAX_RETRY_COUNT;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description RetryConsumer
 * @DateTime 2024/1/9 11:20
 */
@Slf4j
@Component
public class RetryConsumer extends CommonQueueConsumer {

    private final RedisTemplate<String, Object> redisTemplate;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RetryConsumer(RedisTemplate<String, Object> redisTemplate, RabbitTemplate rabbitTemplate) {
        this.redisTemplate = redisTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 根据消息实体类型执行对应业务
     *
     * @param entity 消息实体
     */
    @Override
    public void consume(MessageQueueEntity entity) {
        String key = (String) entity.getData();
        String deleteKey = DELETE_COUNT + key;
        try {
            MyRedisTemplate.del(key);
            redisTemplate.delete(deleteKey);
        } catch (Exception e) {
//            将删除时发生异常则将删除的key重新入队删除，需要判断是否到达最大重试次数，如果到达则不再入队转而发送消息提醒异常
            String temCount = (String) redisTemplate.opsForValue().get(deleteKey);
            Integer count = temCount == null ? 0 : Integer.parseInt(temCount);
            if (count < MAX_RETRY_COUNT) {
//            入队，重试次数加一
                count += 1;
                rabbitTemplate.convertAndSend(COMMON_EXCHANGE, COMMON_ROUTING_KEY, MessageQueueEntity.build(BusinessType.RETRY, key));
                redisTemplate.opsForValue().set(deleteKey, String.valueOf(count));
                redisTemplate.expire(deleteKey, 5L, TimeUnit.SECONDS);
            } else {
                log.error("删除消息异常，消息key为 {} ,请手动查看", key);
            }
        }
    }

    /**
     * 设置此消费队列被匹配到的名称
     */
    @Override
    protected BusinessType getConsumerName() {
        return BusinessType.RETRY;
    }
}
