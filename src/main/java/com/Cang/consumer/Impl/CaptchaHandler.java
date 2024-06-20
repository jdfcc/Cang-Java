package com.Cang.consumer.Impl;

import cn.hutool.core.util.RandomUtil;
import com.Cang.entity.MessageQueueEntity;
import com.Cang.enums.BusinessType;
import com.Cang.service.MailService;
import com.Cang.consumer.CommonQueueHandler;
import com.Cang.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.Cang.constants.RedisConstants.LOGIN_CODE_KEY;
import static com.Cang.constants.RedisConstants.LOGIN_CODE_TTL;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description CaptchaConsumer
 * @DateTime 2024/1/9 9:07
 */
@Slf4j
@Component
public class CaptchaHandler extends CommonQueueHandler {
    private final StringRedisTemplate stringRedisTemplate;
    private final MailService mailService;

    @Autowired
    public CaptchaHandler(StringRedisTemplate stringRedisTemplate, MailService mailService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.mailService = mailService;
    }


    /**
     * 根据消息实体类型执行对应业务
     *
     * @param entity 消息实体
     */
    @Override
    public void consume(MessageQueueEntity entity) {
        String mail = (String) entity.getData();
        //    生成验证码
        String code = RandomUtil.randomNumbers(4);
        //    储存进redis
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + mail, code);
        log.info("验证码为: {}", code);
        //    设置验 证码过期时间
        stringRedisTemplate.expire(LOGIN_CODE_KEY + mail, LOGIN_CODE_TTL, TimeUnit.MINUTES);
        //    发送验证码
        mailService.sendVerFicationMail(mail, code);
    }

    /**
     * 获取消费队列被匹配到的名称
     */
    @Override
    protected BusinessType getConsumerName() {
        return BusinessType.CAPTCHA;
    }


}
