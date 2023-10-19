package com.Cang.consumer;

import com.Cang.template.MyRedisTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.Cang.constants.RabbitMqConstants.*;
import static com.Cang.constants.RedisConstants.DELETE_COUNT;
import static com.Cang.constants.RedisConstants.MAX_RETRY_COUNT;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description RetryQueueConsumer
 * @DateTime 2023/8/7 15:13
 */
@Component
@Slf4j
public class RetryQueueConsumer {

    private final RedisTemplate<String, Object> redisTemplate;

    private final RabbitTemplate rabbitTemplate;

    public RetryQueueConsumer(RedisTemplate<String, Object> redisTemplate, RabbitTemplate rabbitTemplate) {
        this.redisTemplate = redisTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RETRY_QUEUE)
    public void consume(Message message) {
        String key = new String(message.getBody());
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
                rabbitTemplate.convertAndSend(RETRY_EXCHANGE, RETRY_ROUTING_KEY, key);
                redisTemplate.opsForValue().set(deleteKey, String.valueOf(count));
                redisTemplate.expire(deleteKey,5L, TimeUnit.SECONDS);
            } else {
                log.error("删除消息异常，消息key为 {} ,请手动查看", key);
            }
        }

    }

}
