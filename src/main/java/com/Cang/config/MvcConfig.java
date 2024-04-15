package com.Cang.config;

import com.Cang.filter.LoginInterceptor;
import com.Cang.filter.RefreshTokenInterceptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Jdfcc
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${login.authorization}")
    private Boolean auth;

    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        if (auth) {
            registry.addInterceptor(new RefreshTokenInterceptor(redisTemplate)).excludePathPatterns(
                    "/user/code",
                    "/user/login",
                    "/shop/**",
                    "/voucher/**",
// TODO 测试用，开发完成删除
//                    "/shop-type/**",
                    "/upload/**",
//                    "/blog/hot",
//                    "/blog/{id}",
                    "/token/*",
                    "/notice/*",
                    "/ali/**"
            ).order(Ordered.HIGHEST_PRECEDENCE);
//            registry.addInterceptor(new RefreshTokenInterceptor(redisTemplate)).addPathPatterns("/**").order(0);
            registry.addInterceptor(new LoginInterceptor())
                    .excludePathPatterns(
                            "/user/code",
                            "/user/login",
                            "/shop/**",
                            "/voucher/**",
                            "/shop-type/**",
                            "/upload/**",
                            "/blog/hot",
                            "/blog/{id}",
                            "/token/*",
                            "/notice/*",
                            "/ali/**"
                    ).order(Ordered.LOWEST_PRECEDENCE);
        }
    }
}
