package com.Cang.error;

import com.Cang.dto.Result;
import com.Cang.exception.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Jdfcc
 * @Description TODO
 * @DateTime 2023/5/28 10:28
 */
@Slf4j
@RestControllerAdvice
public class SQLExceptionHandler {

    @ExceptionHandler(SQLException.class)
    public Result SQLExceptionHandler(HttpServletRequest request, Throwable e) {
        log.error(e.getMessage());
        return Result.fail("SQL异常" + e.getMessage());
    }

}
