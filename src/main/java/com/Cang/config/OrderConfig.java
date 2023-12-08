package com.Cang.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;

import static com.Cang.constants.RabbitMqConstants.*;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description OrderConfig
 * @DateTime 2023/10/18 20:43
 */
public class OrderConfig {

    private static final String EXCHANGE_TYPE = "x-delayed-message";

    @Bean
    public Queue createDelayOrderQueue() {
        return QueueBuilder.durable(DELAY_ORDER_QUEUE).build();
    }

    @Bean
    public CustomExchange createDelayOrderExchange() {
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put("x-delayed-type", "direct");
        return new CustomExchange(DELAY_ORDER_EXCHANGE, EXCHANGE_TYPE, true, false, arguments);
    }
}
