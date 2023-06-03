package com.Cang.error;

import com.Cang.dto.Result;
import com.Cang.exception.EmptyUserHolderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Jdfcc
 * @Description 处理UserHolder抛出的异常
 * @DateTime 2023/5/27 19:22
 */

@Slf4j
@RestControllerAdvice
public class UserHolderExceptionHandler {

    @ExceptionHandler(EmptyUserHolderException.class)
    public Result UserHolderNullPointerExceptionRun(HttpServletRequest request, Throwable e) {

        log.error("用户未登录: {}", e.toString());
        return Result.fail("用户未登录" );
    }
}
