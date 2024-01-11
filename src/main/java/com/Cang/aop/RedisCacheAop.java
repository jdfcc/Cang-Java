package com.Cang.aop;

import com.Cang.annotations.RedisCache;
import com.Cang.enums.RedisCacheType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description RedisCacheAnnotation Aop
 * @DateTime 2024/1/10 14:04
 */
@Aspect
@Component
@Order(0)
@Slf4j
public class RedisCacheAop {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Data
    private class HashObject {
        private String methodName;
        private Object[] args;
        private String value;
    }

    @Around("@annotation(com.Cang.annotations.RedisCache)")
    public Object logPointCut(ProceedingJoinPoint pjp) throws Throwable {
        Object obj;
        MethodSignature methodSignature;
        try {
            // 获取方法的签名
            methodSignature = (MethodSignature) pjp.getSignature();

            // 获取方法
            Method method = methodSignature.getMethod();

            // 获取方法的类型（返回类型）
            Class<?> returnType = method.getReturnType();
            // 执行原始方法
            obj = pjp.proceed();
            if (returnType.equals(Void.class)) {
                // 方法没有返回值，不做缓存
                log.info("方法没有返回值，不做缓存");
//                return obj;
            }
        } catch (Throwable e) {
            throw new Exception(e.getMessage());
        }
        Signature signature = pjp.getSignature();

        RedisCache annotation = null;
        // 遍历方法数组
        Method method = methodSignature.getMethod();
        if (method.isAnnotationPresent(RedisCache.class)) {
            annotation = method.getAnnotation(RedisCache.class);
        }
        assert annotation != null;
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();
        Object[] args = pjp.getArgs();
        String value = annotation.value();
        HashObject hashObject = new HashObject();
        hashObject.setArgs(args);
        hashObject.setValue(value);
        hashObject.setMethodName(methodName);
        int hashCode = hashObject.hashCode();
        RedisCacheType type = annotation.type();
//        // TODO 根据类型缓存消息
        return obj;
    }

}
