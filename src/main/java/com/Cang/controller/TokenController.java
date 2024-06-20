package com.Cang.controller;

import com.Cang.dto.Result;
import com.Cang.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description TokenController
 * @DateTime 2024/1/5 16:42
 */
@Slf4j
@RestController
@RequestMapping("/token")
public class TokenController {
    /**
     * 通过此接口对accessToken进行续签
     * @param refreshToken token refresh token
     * @return accessToken
     */
    @GetMapping("/verify")
    public Result generateToken(@RequestParam("refreshToken") String refreshToken) throws Exception {
        Long userid = TokenUtil.verifyRefreshToken(refreshToken);
        String accessToken = TokenUtil.generateToken(userid);
        log.info("Generating access token: {}", accessToken);
        return Result.ok(accessToken);
    }
}
