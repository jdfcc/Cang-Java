package com.Cang.strategy;


import com.Cang.consumer.MessageQueueConsumer;
import com.Cang.entity.MessageQueueEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

import static com.Cang.constants.RabbitMqConstants.CAPTCHA_QUEUE;


/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description CommonQueueStrategy
 * @DateTime 2024/1/8 17:12
 */
@Component
@RabbitListener(queues = CAPTCHA_QUEUE)
@Slf4j
public class CommonQueueStrategy {

    @RabbitHandler
    public void consumer(@Payload MessageQueueEntity entity) {
        LinkedHashMap<String, MessageQueueConsumer> map = new LinkedHashMap<String, MessageQueueConsumer>();
        for (MessageQueueConsumer strategy : MessageQueueConsumer.values()) {
            map.put(strategy.name(), strategy);
        }
        map.get(entity.getBusinessType()).consume(entity);
    }


}
