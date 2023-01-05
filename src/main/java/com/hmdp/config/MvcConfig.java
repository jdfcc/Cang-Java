package com.hmdp.config;

import com.hmdp.utils.LoginInterceptor;
import com.hmdp.utils.RefreshTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${login.authorization}")
    private Boolean auth;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        if (auth){
            registry.addInterceptor(new LoginInterceptor())
                    .excludePathPatterns(
                            "/user/code",
                            "/user/login",
                            "/shop/**",
                            "/voucher/**",
                            "/shop-type/**",
                            "/upload/**",
                            "/blog/hot"
                    ).order(1);
        }
        registry.addInterceptor(new RefreshTokenInterceptor(redisTemplate)).addPathPatterns("/**").order(0);
    }
}
