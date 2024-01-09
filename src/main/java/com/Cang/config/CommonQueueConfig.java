package com.Cang.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import static com.Cang.constants.RabbitMqConstants.*;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description CommonQueueConfig 此队列处理业务大致相同的消息实体
 * @DateTime 2024/1/9 11:25
 */
@Component
public class CommonQueueConfig {


    /**
     * 创建消息队列
     */
    @Bean
    public Queue createCommonQueue() {
        return QueueBuilder.nonDurable(COMMON_QUEUE).build();
    }

    /**
     * 创建交换机
     */
    @Bean
    public DirectExchange createCommonDirectExchange() {
        return new DirectExchange(COMMON_EXCHANGE);
    }

    @Bean
    public Binding createCommonQueueBinding(@Qualifier("createCommonQueue") Queue queue,
                                             @Qualifier("createCommonDirectExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(COMMON_ROUTING_KEY);

    }

}
