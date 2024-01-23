package com.Cang.repo;

import com.Cang.consumer.CommonQueueHandler;
import com.Cang.enums.BusinessType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description BaseRepo 存储枚举类型为key，抽象类为value的对象
 * @DateTime 2024/1/10 16:30
 */
public abstract class BaseConsumerRepo {
    public static final Map<BusinessType, List<CommonQueueHandler>> QUEUE_CONSUMERS = new ConcurrentHashMap<>();

    public static List<CommonQueueHandler> getQueueConsumer(BusinessType businessType) {
        if (QUEUE_CONSUMERS.get(businessType) == null) {
            throw new NullPointerException("please be sure that the business handler has been injected");
        } else {
            return QUEUE_CONSUMERS.get(businessType);
        }
    }

    public static void setQueueConsumer(BusinessType businessType, CommonQueueHandler queueConsumer) {
        if (QUEUE_CONSUMERS.containsKey(businessType)) {
            List<CommonQueueHandler> queueConsumers = QUEUE_CONSUMERS.get(businessType);
            queueConsumers.add(queueConsumer);
            QUEUE_CONSUMERS.put(businessType, queueConsumers);
        } else {
            QUEUE_CONSUMERS.put(businessType, new ArrayList<CommonQueueHandler>() {{
                add(queueConsumer);
            }});
        }
    }
}
