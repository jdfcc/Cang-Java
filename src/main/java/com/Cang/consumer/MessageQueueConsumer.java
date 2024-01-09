package com.Cang.consumer;

import cn.hutool.core.util.RandomUtil;
import com.Cang.entity.MessageQueueEntity;
import com.Cang.entity.MyLog;
import com.Cang.service.MailService;
import com.Cang.template.MyRedisTemplate;
import com.Cang.utils.FileUtil;
import com.Cang.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static com.Cang.constants.RabbitMqConstants.RETRY_EXCHANGE;
import static com.Cang.constants.RabbitMqConstants.RETRY_ROUTING_KEY;
import static com.Cang.constants.RedisConstants.*;
import static com.Cang.constants.RedisConstants.MAX_RETRY_COUNT;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description MessageQueueConsumer
 * @DateTime 2024/1/8 16:55
 */
@Component
@Slf4j
public enum MessageQueueConsumer implements Serializable {

    captchaConsumer {
        private StringRedisTemplate stringRedisTemplate;
        private MailService mailService;

        @Autowired
        private void captchaConsumer(StringRedisTemplate stringRedisTemplate, MailService mailService) {
            this.stringRedisTemplate = stringRedisTemplate;
            this.mailService = mailService;
        }

        @Override
        public void consume(MessageQueueEntity entity) {
            String email = (String) entity.getData();
            Long userId = UserHolder.getUser();
            System.out.println("************" + userId);
            Thread thread = Thread.currentThread();
            long id = thread.getId();
            System.out.println("消费者" + id);
            //    生成验证码
            String code = RandomUtil.randomNumbers(4);

            //    储存进redis
            stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + email, code);
            log.info("验证码为: {}", code);
            //    设置验 证码过期时间
            stringRedisTemplate.expire(LOGIN_CODE_KEY + email, LOGIN_CODE_TTL, TimeUnit.MINUTES);
            //    发送验证码
            mailService.sendVerFicationMail(email, code);
        }

    },

    LogQueueConsumer {
        @Value("${my-log.src}")
        private String src;

        @Override
        public void consume(MessageQueueEntity entity) {

            MyLog myLog = (MyLog) entity.getData();

            String value = myLog.getValue();

            LocalDate currentDate = LocalDate.now();
            int month = currentDate.getMonthValue();
            Integer year = currentDate.getYear();
            int day = currentDate.getDayOfMonth();

            String path = src + year + "/" + month + "/";
            String fileName = year + "-" + month + "-" + day;
            value = value + "spend: " + myLog.getTime();
            FileUtil.writeFile(path, fileName, value, "log");
        }
    },
    retryQueueConsumer {


        private RedisTemplate<String, Object> redisTemplate;

        private RabbitTemplate rabbitTemplate;

        @Autowired
        private void retryQueueConsumer(RedisTemplate<String, Object> redisTemplate, RabbitTemplate rabbitTemplate) {
            this.redisTemplate = redisTemplate;
            this.rabbitTemplate = rabbitTemplate;
        }

        @Override
        public void consume(MessageQueueEntity entity) {
            String key = (String) entity.getData();
            String deleteKey = DELETE_COUNT + key;
            try {
                MyRedisTemplate.del(key);
                redisTemplate.delete(deleteKey);
            } catch (Exception e) {
//            将删除时发生异常则将删除的key重新入队删除，需要判断是否到达最大重试次数，如果到达则不再入队转而发送消息提醒异常
                String temCount = (String) redisTemplate.opsForValue().get(deleteKey);
                Integer count = temCount == null ? 0 : Integer.parseInt(temCount);
                if (count < MAX_RETRY_COUNT) {
//            入队，重试次数加一
                    count += 1;
                    rabbitTemplate.convertAndSend(RETRY_EXCHANGE, RETRY_ROUTING_KEY, MessageQueueEntity.build(MessageQueueConsumer.retryQueueConsumer, key));
                    redisTemplate.opsForValue().set(deleteKey, String.valueOf(count));
                    redisTemplate.expire(deleteKey, 5L, TimeUnit.SECONDS);
                } else {
                    log.error("删除消息异常，消息key为 {} ,请手动查看", key);
                }
            }
        }

    };

    public abstract void consume(MessageQueueEntity entity);
}
