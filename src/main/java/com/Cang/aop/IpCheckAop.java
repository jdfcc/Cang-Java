package com.Cang.aop;

import com.Cang.annotations.IpCheckAnnotation;
import com.Cang.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


/**
 * @author Jdfcc
 * @Description IpCheckAop
 * @DateTime 2023/6/28 12:27
 */

@Aspect
@Component
@Order(0)
@Slf4j
public class IpCheckAop {

    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Around("@annotation(com.Cang.annotations.IpCheckAnnotation)")
    public Object checkIpCut(ProceedingJoinPoint pjp) throws Throwable {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String ipAddress = HttpUtil.getIpAddress(request);
        Class<?> clazz = pjp.getTarget().getClass();
        IpCheckAnnotation annotation = clazz.getAnnotation(IpCheckAnnotation.class);
//        判断类上是否有此注释
        if (annotation != null) {
            return pjp.proceed();
        }

        // 获取类中所有的方法
        Method[] methods = clazz.getDeclaredMethods();
        // 遍历方法数组
        for (Method method : methods) {
            // 判断方法是否有注解
            if (method.isAnnotationPresent(IpCheckAnnotation.class)) {
                // 获取方法上的注解
                annotation = method.getAnnotation(IpCheckAnnotation.class);
            }
        }
        Boolean isValid = this.validAndHandleIp(ipAddress, annotation);
        if (!isValid) {
            return null;
        }
        return pjp.proceed();
    }

    private Boolean validAndHandleIp(String ip, IpCheckAnnotation annotation) {
        Integer lastCount = (Integer) redisTemplate.opsForHash().get(ip, "count");

        Integer lastSec = (Integer) redisTemplate.opsForHash().get(ip, "time");
        LocalDateTime now = LocalDateTime.now();
        Long nowSec = now.toEpochSecond(ZoneOffset.UTC);

        if (lastCount == null || lastSec == null) {
            redisTemplate.opsForHash().put(ip, "count", 1);
            redisTemplate.opsForHash().put(ip, "time", nowSec);
            return true;
        }

        Integer limitCount = annotation.count();
        if (limitCount <= 0) {
            throw new IllegalArgumentException("Count can not be 0 and even smaller");
        }
        Integer time = annotation.time();

        Integer step = time / limitCount;

        //        允许访问
        long durSec = nowSec - lastSec;

//            按照步长减少访问次数
        Integer tem = Math.toIntExact((durSec / step));
        lastCount -= tem;


        if ((lastCount ) >= limitCount && nowSec >= lastSec) {
//            规定时间类内问次数达到上限，不能访问。考虑通过步长减少访问次数

            return false;
        }

        lastCount = lastCount > 0 ? lastCount : 0;
        redisTemplate.opsForHash().put(ip, "count", lastCount + 1);
        redisTemplate.opsForHash().put(ip, "time", nowSec);

        return true;
    }


}
