package com.Cang.filter;


import com.Cang.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

/**
 * @author Jdfcc
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        if (UserHolder.getUser() == null) {
            response.setStatus(SC_UNAUTHORIZED);
            log.info("SC_UNAUTHORIZED");
            return false;
        }
        log.info("放行");
        return true;
    }

}
