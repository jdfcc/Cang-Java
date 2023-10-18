package com.Cang.service.impl;

import com.Cang.entity.DoubleToken;
import com.Cang.service.TokenService;
import com.Cang.utils.TokenUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static com.Cang.constants.RedisConstants.LOGIN_USER_KEY;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description TokenServiceImplTest
 * @DateTime 2023/10/18 11:14
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TokenServiceImplTest {
    @Resource
    TokenService tokenService;
    @Resource
    StringRedisTemplate stringRedisTemplate;


    @Test
    public void testTokenService() {
        int count = tokenService.count();
        System.out.println(count);
        DoubleToken byId = tokenService.getById(1);
        System.out.println(byId);
    }

    @Test
    public void testTokenInsert() throws Exception {
        DoubleToken andSaveToken = tokenService.createAndSaveToken(12L);
        System.out.println(andSaveToken);
    }

    @Test
    void testRedisInsert() throws Exception {
        DoubleToken andSaveToken = tokenService.createAndSaveToken(13L);
        String accessToken = andSaveToken.getAccessToken();
        stringRedisTemplate.opsForHash().put(LOGIN_USER_KEY,accessToken,"redis");
    }

}