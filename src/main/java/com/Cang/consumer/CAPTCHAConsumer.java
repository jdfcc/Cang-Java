package com.Cang.consumer;

import cn.hutool.core.util.RandomUtil;
import com.Cang.dto.UserDTO;
import com.Cang.service.MailService;
import com.Cang.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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
public class CAPTCHAConsumer {

    private final StringRedisTemplate stringRedisTemplate;
    private final MailService mailService;

    @Autowired
    public CAPTCHAConsumer(StringRedisTemplate stringRedisTemplate, MailService mailService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.mailService = mailService;
    }

    @RabbitListener(queues = CAPTCHA_QUEUE)
    public void consumer(Message message) {

//        UserDTO user = UserHolder.getUser();
//        System.out.println("************"+user.getId());

        log.info("收到了消息: " + new String(message.getBody()));
        String phone = new String(message.getBody());
        //    生成验证码
        String code = RandomUtil.randomNumbers(4);

        //    储存进redis
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + phone, code);
        log.info("验证码为: {}", code);
        //    设置验证码过期时间
        stringRedisTemplate.expire(LOGIN_CODE_KEY + phone, LOGIN_CODE_TTL, TimeUnit.MINUTES);
        //    发送验证码
        mailService.sendVerFicationMail(phone, code);

    }


}
