package com.Cang.handler;

import com.Cang.exception.DeleteException;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

import static com.Cang.constants.RabbitMqConstants.RETRY_EXCHANGE;
import static com.Cang.constants.RabbitMqConstants.RETRY_ROUTING_KEY;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description 抛出DeleteException时将key放入消息队列重试删除
 * @DateTime 2023/8/8 13:47
 */
@RestControllerAdvice
@Slf4j
public class DeleteExceptionHandler {

    private final RabbitTemplate rabbitTemplate;

    public DeleteExceptionHandler(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @ExceptionHandler(value = DeleteException.class)
    public void handle( Throwable e) {
        String key = e.getMessage();
        rabbitTemplate.convertAndSend(RETRY_EXCHANGE,RETRY_ROUTING_KEY,key);
    }
}
