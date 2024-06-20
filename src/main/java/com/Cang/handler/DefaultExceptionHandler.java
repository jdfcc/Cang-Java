package com.Cang.handler;


import com.Cang.dto.Result;
import com.Cang.entity.MessageQueueEntity;
import com.Cang.enums.BusinessType;
import com.Cang.exception.*;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.core.Ordered;


import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.Cang.constants.RabbitMqConstants.*;


/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description ExceptionHandler
 * @DateTime 2024/1/3 10:51
 */
//@RestControllerAdvice
@Slf4j
public class DefaultExceptionHandler {

    private final RabbitTemplate rabbitTemplate;

    public DefaultExceptionHandler(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ExceptionHandler(value = DeleteException.class)
    public void handle(Throwable e) {
        String key = e.getMessage();
        rabbitTemplate.convertAndSend(COMMON_EXCHANGE, COMMON_ROUTING_KEY, MessageQueueEntity.build(BusinessType.LOG, key));
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ExceptionHandler(MessageException.class)
    public Result handleMessageException(Throwable e) {
        e.printStackTrace();
        return Result.fail(e.getMessage());
    }

//    @Order(Ordered.HIGHEST_PRECEDENCE)
//    @ExceptionHandler(value = EmptyUserHolderException.class)
//    public Result userHolderNullPointerExceptionRun(Throwable e,HttpServletResponse response) {
//        e.printStackTrace();
//        log.error("用户未登录: {}", e.toString());
//        return Result.failAndReLogin(response);
//    }

    @ExceptionHandler(SQLException.class)
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public Result sqlExceptionHandler(Throwable e) {
        log.error(e.getMessage());
        return Result.fail("SQL异常" + e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @Order(Ordered.HIGHEST_PRECEDENCE)
    Result onException(MethodArgumentNotValidException e) {
        BindingResult exceptions = e.getBindingResult();
        // 判断异常中是否有错误信息，如果存在就使用异常中的消息，否则使用默认消息
        if (exceptions.hasErrors()) {
            List<ObjectError> errors = exceptions.getAllErrors();
            if (!errors.isEmpty()) {
                // 这里列出了全部错误参数，按正常逻辑，只需要第一条错误即可
                FieldError fieldError = (FieldError) errors.get(0);
                return Result.fail(fieldError.getDefaultMessage());
            }
        }
        return Result.fail(e.getMessage());
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ExceptionHandler({InvalidTokenException.class})
    public Result tokenExceptionHandler(Throwable e,HttpServletResponse response) {
        e.printStackTrace();
        return Result.failAndReLogin(response);
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ExceptionHandler({TokenExpiredException.class})
    public Result tokenExpiredExceptionHandler(Throwable e,HttpServletResponse response) {
        e.printStackTrace();
        return Result.failAndValidToken(response);
    }

    /**
     * refreshToken失效，需要用户重新登录
     */
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ExceptionHandler({InvalidRefreshTokenException.class})
    public Result refreshTokenExceptionHandler(HttpServletResponse response) {
        return Result.failAndReLogin(response);
    }

    /**
     * accessToken失效，需要用户重新登录
     */
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ExceptionHandler({InvalidAccessTokenException.class})
    public Result accessTokenExceptionHandler(HttpServletResponse response) {
        return Result.failAndReLogin(response);
    }

    @ExceptionHandler(Exception.class)
    @Order(Ordered.LOWEST_PRECEDENCE)
    public Result defaultExceptionHandler(Throwable e) {
        System.out.println("*********************************");
        e.printStackTrace();
        return Result.fail("服务器异常，请稍后再试!");
    }

}
