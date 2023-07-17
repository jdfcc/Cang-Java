package com.Cang.config;

import com.Cang.filter.LoginInterceptor;
import com.Cang.filter.RefreshTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RefreshTokenInterceptor(redisTemplate)).addPathPatterns("/**").order(0);

        if (auth){
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
                            "/notice/*"
                    ).order(1);
        }
    }
}
