package com.Cang.controller;

import com.Cang.dto.Result;
import com.Cang.utils.TokenUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    /**
     * 通过此接口对accessToken进行续签
     * @param refreshToken token refresh token
     * @return accessToken
     */
    @PostMapping("/verify")
    public Result generateToken(@RequestParam("refreshToken") String refreshToken) throws Exception {
        Long userid = TokenUtil.verifyRefreshToken(refreshToken);
        String accessToken = TokenUtil.generateToken(userid);
        return Result.ok(accessToken);
    }
}
