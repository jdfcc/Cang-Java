package com.Cang.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import static com.Cang.constants.RabbitMqConstants.*;

/**
 * @author Jdfcc
 * @Description 重试队列配置
 * @DateTime 2023/6/26 14:03
 */

@Component
public class RetryQueueConfig {

    /**
     * 定义队列
     */
    @Bean
    public Queue createRetryQueue() {
        return QueueBuilder.durable(RETRY_QUEUE).build();
    }

    /**
     * 定义交换机
     */
    @Bean
    public DirectExchange createRetryExchange() {
        return ExchangeBuilder.directExchange(RETRY_EXCHANGE).durable(true).build();
    }

    /**
     * 定义路由
     */
    @Bean
    public Binding createRetryQueueBinding(@Qualifier("createRetryQueue") Queue queue,
                                         @Qualifier("createRetryExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(RETRY_ROUTING_KEY);
    }



}
