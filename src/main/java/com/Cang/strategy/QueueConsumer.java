package com.Cang.strategy;

import com.Cang.entity.MessageQueueEntity;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description QueueConsumer
 * @DateTime 2024/1/9 9:05
 */
public abstract class QueueConsumer {

    /**
     * 根据消息实体类型执行对应业务
     * @param entity 消息实体
     */
    public abstract void consume(MessageQueueEntity entity);
}
