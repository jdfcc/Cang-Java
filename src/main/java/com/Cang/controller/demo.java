package com.Cang.controller;

import com.Cang.dto.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description demo
 * @DateTime 2024/4/16 11:05
 */
@RequestMapping("/demo")
@RestController
public class demo {
    @GetMapping("/1")
    public Result demo1(HttpServletResponse response) {
        return Result.failAndReLogin(response);
    }

}
