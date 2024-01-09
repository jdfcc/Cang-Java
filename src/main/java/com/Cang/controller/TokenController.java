package com.Cang.controller;

import com.Cang.dto.Result;
import com.Cang.utils.TokenUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description TokenController
 * @DateTime 2024/1/5 16:42
 */
@RestController
@RequestMapping("/token")
public class TokenController {
    @PostMapping
    public Result generateToken(String refreshToken) throws Exception {
        Long userid = TokenUtil.verifyToken(refreshToken);
        String accessToken = TokenUtil.generateToken(userid);
        return Result.ok(accessToken);
    }
}
