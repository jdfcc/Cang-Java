package com.Cang.consumer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static com.Cang.constants.RabbitMqConstants.RETRY_QUEUE;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description RetryQueueConsumer
 * @DateTime 2023/8/7 15:13
 */
@Component
public class RetryQueueConsumer {


    @RabbitListener(queues = RETRY_QUEUE)
    public void consume(Message message) {
        String key = new String(message.getBody());
//        MyRedisTemplate.del(key);
    }

}
