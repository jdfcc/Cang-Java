package com.Cang.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import static com.Cang.constants.RabbitMqConstants.*;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description MessageQueueConfig
 * @DateTime 2024/1/8 16:01
 */
public class MessageQueueConfig {


    /**
     * 创建消息队列
     */
    @Bean
    public Queue createMessageQueue() {
        return QueueBuilder.nonDurable(MESSAGE_QUEUE).build();
    }

    /**
     * 创建交换机
     */
    @Bean
    public DirectExchange createMessageDirectExchange() {
        return new DirectExchange(MESSAGE_EXCHANGE);
    }

    @Bean
    public Binding createMessageQueueBinding(@Qualifier("createMessageQueue") Queue queue,
                                             @Qualifier("createMessageDirectExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(MESSAGE_ROUTING_KEY);

    }
}
