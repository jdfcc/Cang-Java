package com.Cang.strategy;


import com.Cang.consumer.CommonQueueHandler;
import com.Cang.entity.MessageQueueEntity;
import com.Cang.repo.child.CommonQueueConsumerRepo;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;


import static com.Cang.constants.RabbitMqConstants.COMMON_QUEUE;


/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description CommonQueueStrategy，选择消费者消费CommonQueue过来的消息
 * @DateTime 2024/1/8 17:12
 */
@Component
@RabbitListener(queues = COMMON_QUEUE)
public class CommonQueueConsumeStrategy {

    @RabbitHandler
    public void consume(@Payload MessageQueueEntity entity) {
        List<CommonQueueHandler> queueConsumers = CommonQueueConsumerRepo.getQueueConsumer(entity.getBusinessType());
        for (CommonQueueHandler queueConsumer : queueConsumers) {
            queueConsumer.consume(entity);
        }
    }


}
