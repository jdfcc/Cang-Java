package com.Cang.controller;

import com.Cang.annotations.IpCheckAnnotation;
import com.Cang.annotations.LogAnnotation;
import com.Cang.dto.Result;
import com.Cang.exception.DeleteException;
import com.Cang.exception.InvalidTokenException;
import com.Cang.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


/**
 * @author Jdfcc
 * @Description testController
 * @DateTime 2023/6/26 11:48
 */

@RestController
@Slf4j
@RequestMapping("/test")
@IpCheckAnnotation(count = 5,time = 100)
public class testController {

    @GetMapping("/name/{name}")
    @LogAnnotation
//    @IpCheckAnnotation(count = 5,time = 100)
    public Result deleteBlogImg(@PathVariable("name") String name) {

        return Result.ok(name);
    }

    @GetMapping("")
//    @ResponseStatus(code= HttpStatus.INTERNAL_SERVER_ERROR,reason="server error")
    public Result test() {
        return Result.ok();
    }

    @GetMapping("/token")
    public Result testToken() {
        throw new InvalidTokenException("Token is not valid");
    }

}
