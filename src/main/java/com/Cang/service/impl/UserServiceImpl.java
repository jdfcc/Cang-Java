package com.Cang.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.Cang.utils.IdGeneratorSnowflake;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.Cang.dto.LoginFormDTO;
import com.Cang.dto.Result;
import com.Cang.dto.UserDTO;
import com.Cang.entity.User;
import com.Cang.mapper.UserMapper;
import com.Cang.service.IUserService;
import com.Cang.utils.RegexUtils;
import com.Cang.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;



import static com.Cang.constants.RabbitMqConstants.CAPTCHA_EXCHANGE;
import static com.Cang.constants.RabbitMqConstants.CAPTCHA_ROUTING_KEY;
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

    public UserServiceImpl(UserMapper mapper, RabbitTemplate rabbitTemplate) {
        this.mapper = mapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Result sendCode(String phone, HttpSession session) {
//        验证手机号是否合法 TODO 取消手机号注册功能，改用邮箱注册。需要实现一个校验邮箱是否合法的工具类来实现此功能
//        if (RegexUtils.isPhoneInvalid(phone)) {
//            return Result.fail("手机号格式有误");
//        }
//        发送邮箱至消息队列
        rabbitTemplate.convertAndSend( CAPTCHA_EXCHANGE,CAPTCHA_ROUTING_KEY, phone);

//        返回
        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO dto, HttpSession session) {
        //        校验手机号
        String phone = dto.getPhone();
        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail("手机号格式有误");
        }
        //校验验证码
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + phone);
        String code = dto.getCode();
        if (cacheCode == null || !cacheCode.equals(code)) {
            return Result.fail("验证码有误");
        }


        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User user = mapper.selectOne(wrapper);

//        校验密码
//        String password=dto.getPassword();
//
//        if((!user.getPassword().equals(password))&&password!=null)
//            return Result.fail("密码错误");

        //        判断用户是否存在，不存在则注册
//        TODO 注册用户
        if (user == null) {
            user = new User();
            user.setPhone(phone);
//            通过雪花算法生成用户id
            user.setId(new IdGeneratorSnowflake().snowflakeId());
            user.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomString(5));
            log.info("%%%%%%%%% {}", user);
            mapper.insert(user);
        }
        UserDTO date = BeanUtil.copyProperties(user, UserDTO.class);
        String token = UUID.randomUUID().toString(true);
//        将dto转换成map
        Map<String, Object> userMap = BeanUtil.beanToMap(date, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));

//        储存进redis
        stringRedisTemplate.opsForHash().putAll(LOGIN_USER_KEY + token, userMap);
        stringRedisTemplate.expire(LOGIN_USER_KEY + token, LOGIN_USER_TTL, TimeUnit.MINUTES);
//        将token和用户id返回给前端
        user = mapper.selectOne(wrapper);
        Long id = user.getId();
        log.info("登录用户id为: {}", id);
        return Result.ok(token, id);
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
