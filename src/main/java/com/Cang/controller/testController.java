package com.Cang.controller;

import com.Cang.annotations.LogAnnotation;
import com.Cang.dto.Result;
import org.springframework.web.bind.annotation.*;


/**
 * @author Jdfcc
 * @Description testController
 * @DateTime 2023/6/26 11:48
 */

@RestController
@RequestMapping("/test")
public class testController {

    @GetMapping("/name/{name}")
    @LogAnnotation
    public Result deleteBlogImg(@PathVariable("name") String name) {
        return Result.ok(name);
    }

    @GetMapping("")
    public Result test() {
        return Result.ok();
    }
}
