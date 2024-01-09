package com.Cang.repo;

import com.Cang.enums.BusinessType;
import com.Cang.consumer.CommonQueueConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description RegistCommonQueueConsumerConfig，存储普通消息队列消费者
 * @DateTime 2024/1/9 10:39
 */
public class CommonQueueConsumerRepo {
    public static final Map<BusinessType, List<CommonQueueConsumer>> QUEUE_CONSUMERS = new ConcurrentHashMap<>();

    public static List<CommonQueueConsumer> getQueueConsumer(BusinessType businessType) {
        return QUEUE_CONSUMERS.get(businessType);
    }

    public static void setQueueConsumer(BusinessType businessType, CommonQueueConsumer queueConsumer) {
        if (QUEUE_CONSUMERS.containsKey(businessType)) {
            List<CommonQueueConsumer> queueConsumers = QUEUE_CONSUMERS.get(businessType);
            queueConsumers.add(queueConsumer);
            QUEUE_CONSUMERS.put(businessType, queueConsumers);
        } else {
            QUEUE_CONSUMERS.put(businessType, new ArrayList<CommonQueueConsumer>() {{
                add(queueConsumer);
            }});
        }
    }
}
