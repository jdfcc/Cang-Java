package com.Cang.service.impl;

import com.Cang.dto.Result;
import com.Cang.entity.User;
import com.Cang.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Jdfcc
 * @Description TODO
 * @DateTime 2023/6/5 19:19
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserServiceImplTest {
    @Autowired
    private UserMapper mapper;

    @Test
    void getAvatar() {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery(User.class);
        wrapper.eq(User::getId, 1010L);
        User user = mapper.selectOne(wrapper);
        String icon = user.getIcon();
        log.info("icon {}", icon);
    }
}