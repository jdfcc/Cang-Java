package com.Cang.aop;

import com.Cang.annotations.IpCheckAnnotation;
import com.Cang.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

import static com.Cang.constants.RedisConstants.IP_CACHE_KEY;


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

    private RedissonClient redissonClient;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate, RedissonClient redissonClient) {
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
    }



    @Around("@annotation(com.Cang.annotations.IpCheckAnnotation) || @within(com.Cang.annotations.IpCheckAnnotation)")
    public Object checkIpCut(ProceedingJoinPoint pjp) throws Throwable {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String ipAddress = HttpUtil.getIpAddress(request);

        Class<?> clazz = pjp.getTarget().getClass();
        IpCheckAnnotation annotation = clazz.getAnnotation(IpCheckAnnotation.class);

        // 判断类上是否有此注释，如果有这个注解，则不再判断方法里是否有此注解，因为作用粒度覆盖了整个类
        if (annotation != null) {
            RLock lock = redissonClient.getLock(ipAddress);
            lock.lock();
            Boolean isValid = this.validAndHandleIp(ipAddress, annotation);
            lock.unlock();
            if (!isValid) {
                return null;
            }
            return pjp.proceed();
        }

        // 获取类中所有的方法，判断是否存在此注解
        Method[] methods = clazz.getDeclaredMethods();
        // 遍历方法数组
        for (Method method : methods) {
            // 判断方法是否有注解
            if (method.isAnnotationPresent(IpCheckAnnotation.class)) {
                // 获取方法上的注解
                annotation = method.getAnnotation(IpCheckAnnotation.class);
            }
        }
        if (annotation == null) {
            //        放行
            return pjp.proceed();
        }
        RLock lock = redissonClient.getLock(ipAddress);
        lock.lock();
        Boolean isValid = this.validAndHandleIp(ipAddress, annotation);
        lock.unlock();
        if (!isValid) {
            return null;
        }
        return pjp.proceed();
    }

    private Boolean validAndHandleIp(String ip, IpCheckAnnotation annotation) {
        String key=IP_CACHE_KEY+ip;

        Integer lastCount = (Integer) redisTemplate.opsForHash().get(key, "count");
        int limitCount = annotation.count();


        Integer lastSec = (Integer) redisTemplate.opsForHash().get(key, "time");
        LocalDateTime now = LocalDateTime.now();
        Long nowSec = now.toEpochSecond(ZoneOffset.UTC);

        int time = annotation.time();

        if (lastCount == null || lastSec == null) {
            redisTemplate.opsForHash().put(key, "count", 1);
            redisTemplate.opsForHash().put(key, "time", nowSec);
            redisTemplate.expire(key, time, TimeUnit.SECONDS);
            return true;
        }

        if (limitCount <= 0) {
            throw new IllegalArgumentException("Count can not be 0 and even smaller");
        }

        //        允许访问
        int step = time / limitCount;

        long durSec = nowSec - lastSec;

        //        按照步长减少访问次数
        int tem = Math.toIntExact((durSec / step));

        lastCount -= tem;

        if ((lastCount) >= limitCount && nowSec >= lastSec) {
//            规定时间内问次数达到上限，不能访问。过期时间为步长.
            return false;
        }

        lastCount = lastCount > 0 ? lastCount : 0;
        redisTemplate.opsForHash().put(key, "count", lastCount + 1);
        redisTemplate.opsForHash().put(key, "time", nowSec);
        redisTemplate.expire(key, time, TimeUnit.SECONDS);
        return true;
    }


}
