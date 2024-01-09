package com.Cang.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.Cang.constants.TokenConstant;
import com.Cang.entity.DoubleToken;
import com.Cang.entity.MessageQueueEntity;
import com.Cang.enums.BusinessType;
import com.Cang.service.TokenService;
import com.Cang.utils.IdGeneratorSnowflake;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.Cang.dto.LoginFormDTO;
import com.Cang.dto.Result;
import com.Cang.entity.User;
import com.Cang.mapper.UserMapper;
import com.Cang.service.IUserService;
import com.Cang.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.concurrent.TimeUnit;


import static com.Cang.constants.RabbitMqConstants.*;
import static com.Cang.constants.RedisConstants.*;
import static com.Cang.constants.SystemConstants.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jdfcc
 * @since 2023-1-3
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final UserMapper mapper;


    private RabbitTemplate rabbitTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private TokenService tokenService;

    private static final String WRONG_PATTERN_EMAIL = "错误的邮箱格式";

    public UserServiceImpl(UserMapper mapper, RabbitTemplate rabbitTemplate) {
        this.mapper = mapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Result sendCode(String email, HttpSession session) {
//        TODO 邮箱验证有问题
//        if (RegexUtils.isEmailInvalid(email)) {
//            return Result.fail(WRONG_PATTERN_EMAIL);
//        }
//        UserDTO userDTO = new UserDTO();
//        userDTO.setId(114514L);
//        userDTO.setIcon("user1e2e");
//        UserHolder.saveUser(userDTO);
//
//        Thread thread = Thread.currentThread();
//        long id = thread.getId();
//        System.out.println("生产者" + id);
        rabbitTemplate.convertAndSend(COMMON_EXCHANGE, COMMON_ROUTING_KEY, MessageQueueEntity.build(BusinessType.CAPTCHA, email));
//        返回
        return Result.ok();
    }


    @Override
    public Result login(LoginFormDTO dto, HttpSession session) throws Exception {

        //    TODO 實現手機密碼登錄功能

        //        校验郵箱
        String email = dto.getPhone();
//        TODO 邮箱校验有误
//        if (RegexUtils.isEmailInvalid(email)) {
//            return Result.fail("邮箱格式有误");
//        }
        //校验验证码
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + email);
        String code = dto.getCode();
        if (cacheCode == null || !cacheCode.equals(code)) {
            return Result.fail("验证码有误" + dto.toString() + cacheCode + LOGIN_CODE_KEY + email);
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        User user = mapper.selectOne(wrapper);
        if (user == null) {
            user = createUser(email);
        }
        Long userid = user.getId();
        DoubleToken doubleToken = tokenService.createAndSaveToken(userid);

//        储存进redis
        stringRedisTemplate.opsForValue().
                set(LOGIN_USER_KEY + doubleToken.getAccessToken(), String.valueOf(userid));
        stringRedisTemplate.expire(LOGIN_USER_KEY + doubleToken.getAccessToken(), TokenConstant.EXPIRE_TIME_60_60, TimeUnit.SECONDS);
        //        将token和用户id返回给前端
        Long id = user.getId();
        log.info("登录用户id为: {}", id);
        return Result.ok(doubleToken, id);
    }

    /**
     * 注冊用戶
     */
    User createUser(String email) {
        //         創建用户
        User user = new User();
        user.setEmail(email);
//            通过雪花算法生成用户id
        user.setId(new IdGeneratorSnowflake().snowflakeId());
        user.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomString(5));
        log.info("%%%%%%%%% {}", user);
        mapper.insert(user);
        return user;
    }


    @Override
    public Result logout(HttpServletRequest request) {
        stringRedisTemplate.delete(LOGIN_USER_KEY + request.getHeader(REQUEST_HEAD));
        UserHolder.removeUser();
        return Result.ok();
    }

    @Override
    public Result getAvatar(Long userid) {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery(User.class);
        wrapper.eq(User::getId, userid);
        User user = mapper.selectOne(wrapper);
        String icon = user.getIcon();
        return Result.ok(icon);
    }
}
