package com.Cang.aop;

import com.Cang.dto.Result;
import com.Cang.enums.TokenStatus;
import com.Cang.utils.StatusHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description 如果检测到需要刷新token，则返回给前端的result的状态码就会由此切面变更。
 * @DateTime 2024/1/4 11:28
 */
@Aspect
@Component
@Order()
@Slf4j
public class AccessAop {

    @Order()
    @AfterReturning(pointcut = "execution(* com.Cang.controller..*.*(..))", returning = "result")
    public Result changeStatus(JoinPoint pjp, Result result) throws Throwable {
        if (StatusHolder.getStatus().equals(TokenStatus.ACCESS_TOKEN_EXPIRED)) {
            // 更新状态码，让前端发送请求查询获取token
            result.setStatusCode(999);
        }
        return result;
    }

}
