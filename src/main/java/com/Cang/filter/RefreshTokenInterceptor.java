package com.Cang.filter;


import cn.hutool.core.util.StrUtil;
import com.Cang.enums.TokenStatus;
import com.Cang.utils.StatusHolder;
import com.Cang.utils.TokenUtil;
import com.Cang.utils.UserHolder;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

import static com.Cang.constants.RedisConstants.LOGIN_USER_KEY;
import static com.Cang.constants.SystemConstants.REQUEST_HEAD;

/**
 * @author Jdfcc
 */
@Slf4j
public class RefreshTokenInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        // 1.获取请求头中的token
        String accessToken = request.getHeader(REQUEST_HEAD);
        if (StrUtil.isBlank(accessToken)) {
            return true;
        }
        // 2.基于accessToken或refreshToken获取redis中的用户
        Long userId = null;
        try {
            userId = TokenUtil.verifyAccessToken(accessToken);
        } catch (TokenExpiredException e) {
            // accessToken过期，校验refreshToken是否也过期,设置状态码要求前端发送请求到tokenController验证refreshToken是否过期。
// TODO 完善双token
            StatusHolder.setStatus(TokenStatus.ACCESS_TOKEN_EXPIRED);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        log.info("当前请求用户为 {}", userId);
        // 6.存在，保存用户信息到 ThreadLocal
        UserHolder.saveUser(userId);

        // 8.放行
        return true;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) {
        // 防止线程复用，移除用户
        StatusHolder.removeStatus();
        UserHolder.removeUser();
    }
}
