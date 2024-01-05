package com.Cang.filter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.Cang.constants.TokenConstant;
import com.Cang.dto.UserDTO;
import com.Cang.enums.TokenStatus;
import com.Cang.utils.StatusHolder;
import com.Cang.utils.TokenUtil;
import com.Cang.utils.UserHolder;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.Cang.constants.RedisConstants.LOGIN_USER_KEY;
import static com.Cang.constants.RedisConstants.LOGIN_USER_TTL;
import static com.Cang.constants.SystemConstants.REQUEST_HEAD;

/**
 * @author Jdfcc
 */
@Slf4j
public class RefreshTokenInterceptor implements HandlerInterceptor {

    /**
     * 前端拦截到status为此状态码时会发送请求到tokenController验证refreshToken是否过期
     */
    private static final int VERIFY_REGENERATE_CODE = 40002;

    /**
     * 当refreshToken过期时，认为他的所有token都已过期
     */
    private static final int ALL_TOKEN_EXPIRE=40003;

    private final StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        // 1.获取请求头中的token
        String accessToken = request.getHeader(REQUEST_HEAD);

//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        response.setHeader("jdfcc","this is a refresh token");
//        response.setStatus(VERIFY_REGENERATE_CODE);
//        response.setHeader("Cache-Control", "no-store");

        if (StrUtil.isBlank(accessToken)) {
            return true;
        }
        // 2.基于accessToken或refreshToken获取redis中的用户
        //        TODO 修改为基于双token的判断
        String key = LOGIN_USER_KEY + accessToken;
        String id = stringRedisTemplate.opsForValue().get(key);
        if (id == null || id.isEmpty()) {
            return true;
        }

        Long userId = null;
        try {
            userId = TokenUtil.verifyToken(accessToken);
        } catch (TokenExpiredException e) {
            // accessToken过期，校验refreshToken是否也过期,设置状态码要求前端发送请求到tokenController验证refreshToken是否过期。
            StatusHolder.setStatus(TokenStatus.ACCESS_TOKEN_EXPIRED);
//            return true;
//            TODO token过期，需要客户端重新发送请求刷新accessToken，无感刷新？
            return false;
        }

        if (!id.equals(userId.toString())) {
            log.info("当前token取得的id与预期的id不一致。当前token {}，当前id {}，预期的id {}", accessToken, id, userId);
            return true;
        }

        log.info("当前请求用户为 {}", userId);
        // 6.存在，保存用户信息到 ThreadLocal
        UserHolder.saveUser(userId);
        // 7.刷新token有效期
        stringRedisTemplate.expire(key, TokenConstant.EXPIRE_TIME_60_60, TimeUnit.SECONDS);


        // 8.放行
//        div分支
        return true;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) {
        // 防止线程复用，移除用户
        StatusHolder.removeStatus();
        UserHolder.removeUser();
    }
}
