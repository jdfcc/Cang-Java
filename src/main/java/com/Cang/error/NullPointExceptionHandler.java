package com.Cang.error;

import com.Cang.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Jdfcc
 * @Description 空指针异常捕获处理
 * @DateTime 2023/5/12 15:16
 */

@RestControllerAdvice
@Slf4j
public class NullPointExceptionHandler {
//    @ExceptionHandler(Exception.class)
    public Result nullPointerExceptionRun(HttpServletRequest request,Throwable e) throws Throwable {

        try {
            String message=e.getMessage();
            log.error("Exception {}",message);
        }
        catch (Exception e1){
            throw e;
        }
        finally {
            return Result.fail("服务器异常");
        }

    }

}
