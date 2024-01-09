package com.Cang.strategy;

import cn.hutool.core.util.RandomUtil;
import com.Cang.entity.Blog;
import com.Cang.service.MailService;
import com.Cang.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.Cang.constants.RabbitMqConstants.CAPTCHA_QUEUE;
import static com.Cang.constants.RedisConstants.LOGIN_CODE_KEY;
import static com.Cang.constants.RedisConstants.LOGIN_CODE_TTL;

/**
 * @author Jdfcc
 * @Description CAPTCHAConsumer
 * @DateTime 2023/6/25 21:54
 */

@Component
@Slf4j
@RabbitListener(queues = CAPTCHA_QUEUE)
public class CAPTCHAConsumer {

    private final StringRedisTemplate stringRedisTemplate;
    private final MailService mailService;

    @Autowired
    public CAPTCHAConsumer(StringRedisTemplate stringRedisTemplate, MailService mailService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.mailService = mailService;
    }

    @RabbitHandler
    public void consumer(@Payload String message) {
        Long userId = UserHolder.getUser();
        System.out.println("************" + userId);
        Thread thread = Thread.currentThread();
        long id = thread.getId();
        System.out.println("消费者" + id);
        //    生成验证码
        String code = RandomUtil.randomNumbers(4);

        //    储存进redis
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + message, code);
        log.info("验证码为: {}", code);
        //    设置验 证码过期时间
        stringRedisTemplate.expire(LOGIN_CODE_KEY + message, LOGIN_CODE_TTL, TimeUnit.MINUTES);
        //    发送验证码
        mailService.sendVerFicationMail(message, code);
    }

    @RabbitHandler
        // TODO 添加至枚举类
    void handleBlog(@Payload Blog blog) {
        log.info("**** {}", blog);
    }
}
