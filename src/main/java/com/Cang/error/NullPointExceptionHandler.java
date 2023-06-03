//package com.Cang.error;
//
//import com.Cang.dto.Result;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import javax.servlet.http.HttpServletRequest;
//
///**
// * @author Jdfcc
// * @Description 空指针异常捕获处理
// * @DateTime 2023/5/12 15:16
// */
//
//@RestControllerAdvice
//@Slf4j
//public class NullPointExceptionHandler {
//    @ExceptionHandler(NullPointerException.class)
//    public Result nullPointerExceptionRun(HttpServletRequest request,Throwable e){
//        String message="failed!";
//        log.error("空指针异常: {}",e.toString());
//        return Result.fail("服务器异常"+message);
//    }
//
//}
