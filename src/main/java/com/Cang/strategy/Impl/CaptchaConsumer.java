package com.Cang.strategy.Impl;

import com.Cang.entity.MessageQueueEntity;
import com.Cang.strategy.QueueConsumer;
import org.springframework.stereotype.Component;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description CaptchaConsumer
 * @DateTime 2024/1/9 9:07
 */
@Component
public class CaptchaConsumer extends QueueConsumer {

    /**
     * 根据消息实体类型执行对应业务
     *
     * @param entity 消息实体
     */
    @Override
    public void consume(MessageQueueEntity entity) {

    }

}
