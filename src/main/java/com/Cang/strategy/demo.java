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
public enum demo implements Serializable {
    captchaConsumer {
        @Override
        public void consume(MessageQueueEntity entity) {

        }
    },

    LogQueueConsumer {
        @Override
        public void consume(MessageQueueEntity entity) {

        }
    },
    retryQueueConsumer {
        @Override
        public void consume(MessageQueueEntity entity) {

        }
    };

    public abstract void consume(MessageQueueEntity entity);
}
