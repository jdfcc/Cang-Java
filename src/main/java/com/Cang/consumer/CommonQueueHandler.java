package com.Cang.consumer;

import com.Cang.repo.child.CommonQueueConsumerRepo;
import com.Cang.entity.MessageQueueEntity;
import com.Cang.enums.BusinessType;

import javax.annotation.PostConstruct;


/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description QueueConsumer 消费普通队列过来的消息
 * @DateTime 2024/1/9 9:05
 */

public abstract class CommonQueueHandler {


    /**
     * 根据消息实体类型执行对应消费者
     *
     * @param entity 消息实体
     */
    public abstract void consume(MessageQueueEntity entity);

    /**
     * 设置此消费队列被匹配到的名称
     */
    protected abstract BusinessType getConsumerName();

    @PostConstruct
    void init() {
        CommonQueueConsumerRepo.setQueueConsumer(getConsumerName(), this);
    }
}