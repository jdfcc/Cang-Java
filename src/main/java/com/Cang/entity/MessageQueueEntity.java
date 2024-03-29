package com.Cang.entity;

import com.Cang.enums.BusinessType;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description MessageQueueEntity, 通过此类对mq消息进行传输
 * @DateTime 2024/1/8 16:13
 */
@Data
public class MessageQueueEntity implements Serializable {
    /**
     * 业务类型
     */
    private final BusinessType businessType;

    private final Object data;

    public static MessageQueueEntity build(BusinessType businessType, Object data) {
        return new MessageQueueEntity(businessType, data);
    }

}
