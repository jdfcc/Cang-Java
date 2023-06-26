package com.Cang.consumer;

import cn.hutool.json.JSONUtil;
import com.Cang.annotations.LogAnnotation;
import com.Cang.entity.MyLog;
import com.Cang.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static com.Cang.constants.RabbitMqConstants.LogQueue;

/**
 * @author Jdfcc
 * @Description LogQueueConsumer
 * @DateTime 2023/6/26 14:23
 */

@Component
@Slf4j
public class LogQueueConsumer {

    @Value("${my-log.src}")
    private String src;

    @RabbitListener(queues = LogQueue)
    public void consume(Message message){
        String json = new String(message.getBody());
        MyLog myLog = JSONUtil.toBean(json, MyLog.class);

        String value=myLog.getValue();

        LocalDate currentDate = LocalDate.now();
        Integer month = currentDate.getMonthValue();
        Integer year = currentDate.getYear();
        Integer day = currentDate.getDayOfMonth();

        String path = src + year + "/" + month + "/";
        String fileName = year + "-" + month + "-" + day;
        value=value+"spend: "+myLog.getTime();
        FileUtil.writeFile(path, fileName, value, "log");
    }

}
