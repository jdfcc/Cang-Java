package com.Cang.controller;

import com.Cang.annotations.IpCheckAnnotation;
import com.Cang.annotations.LogAnnotation;
import com.Cang.annotations.RedisCache;
import com.Cang.dto.Result;
import com.Cang.enums.RedisCacheType;
import com.Cang.exception.InvalidTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


/**
 * @author Jdfcc
 * @Description testController
 * @DateTime 2023/6/26 11:48
 */

@RestController
@Slf4j
@RequestMapping("/test")
public class testController {


    @GetMapping("/name/{name}")
    @LogAnnotation
//    @IpCheckAnnotation(count = 5,time = 100)
    public Result deleteBlogImg(@PathVariable("name") String name) {
        return Result.ok(name);
    }

    @GetMapping("/test1")
    @RedisCache(value = "123", type = RedisCacheType.VALUE)
//    @ResponseStatus(code= HttpStatus.INTERNAL_SERVER_ERROR,reason="server error")
    public Result test() {
        return Result.ok();
    }

    @GetMapping("/test2")
    @RedisCache(value = "45676", type = RedisCacheType.LIST)
    public void tet() {
    }

    @GetMapping("/token")
    public Result testToken() {
        throw new InvalidTokenException("Token is not valid");
    }

    @GetMapping()
    @IpCheckAnnotation
    public Result testIp() {
        return Result.ok();
    }

}
