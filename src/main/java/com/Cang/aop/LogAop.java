package com.Cang.aop;


import com.Cang.consumer.MessageQueueConsumer;
import com.Cang.entity.MessageQueueEntity;
import com.Cang.entity.MyLog;
import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Calendar;


import static com.Cang.constants.RabbitMqConstants.LOG_EXCHANGE;
import static com.Cang.constants.RabbitMqConstants.LOG_ROUTING_KEY;


/**
 * @author Jdfcc
 * @Description 通过日志注解打印日志
 * @DateTime 2023/6/26 10:06
 */
@Aspect
@Component
@Order(0)
public class LogAop {

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    @Around("@annotation(com.Cang.annotations.LogAnnotation)")
    public Object logPointCut(ProceedingJoinPoint pjp) {

//        String filePath = "folder/example.txt";
        long startTime = System.currentTimeMillis();
        Object obj = null;
        //
        try {
            obj = pjp.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        LocalTime currentTime = LocalTime.now();
        int minute = currentTime.getMinute();
        int second = currentTime.getSecond();

        Signature signature = pjp.getSignature();
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();


        String time = hour + ":" + minute + ":" + second + "  ";
        assert obj != null;
        String value = time + "method: " + methodName + " inputArgs" + ": " + JSONObject.toJSONString(pjp.getArgs()) + " " + " " + " outputArgs" + ": " + obj;
//        writeFile(JSONObject.toJSONString(pjp.getArgs()),obj.toString());

        MyLog myLog = new MyLog();
        myLog.setValue(value);
        long endTime = System.currentTimeMillis();
        myLog.setTime(endTime - startTime);
        rabbitTemplate.convertAndSend(LOG_EXCHANGE, LOG_ROUTING_KEY, MessageQueueEntity.build(MessageQueueConsumer.LogQueueConsumer, myLog));

        return obj;

    }

}
