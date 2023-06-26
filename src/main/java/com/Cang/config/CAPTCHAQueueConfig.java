package com.Cang.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import static com.Cang.constants.RabbitMqConstants.*;

/**
 * @author Jdfcc
 * @Description 验证码消息队列
 * @DateTime 2023/6/25 21:37
 */

@Component
public class CAPTCHAQueueConfig {


    private RabbitTemplate rabbitTemplate;

    @Autowired
    public CAPTCHAQueueConfig(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 创建消息队列
     *
     * @return
     */
    @Bean
    public Queue createCAPTCHAQueue() {
        return QueueBuilder.nonDurable(CAPTCHAQueue).build();
    }

    /**
     * 创建交换机
     *
     * @return
     */
    @Bean
    public DirectExchange createDirectExchange() {
        return new DirectExchange(CAPTCHAExchange);
    }

    @Bean
    public Binding createCaptchaQueueBinding(@Qualifier("createCAPTCHAQueue") Queue queue,
                                 @Qualifier("createDirectExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(CAPTCHARoutingKey);

    }


}
