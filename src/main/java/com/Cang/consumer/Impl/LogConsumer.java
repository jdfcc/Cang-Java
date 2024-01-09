package com.Cang.consumer.Impl;

import com.Cang.entity.MessageQueueEntity;
import com.Cang.entity.MyLog;
import com.Cang.enums.BusinessType;
import com.Cang.consumer.CommonQueueConsumer;
import com.Cang.utils.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description LogConsumer
 * @DateTime 2024/1/9 11:18
 */
@Component
public class LogConsumer extends CommonQueueConsumer {

    @Value("${my-log.src}")
    private String src;

    /**
     * 根据消息实体类型执行对应业务
     *
     * @param entity 消息实体
     */

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

    /**
     * 设置此消费队列被匹配到的名称
     */
    @Override
    protected BusinessType getConsumerName() {
        return BusinessType.LOG;
    }
}
