package com.hmdp.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import static com.hmdp.utils.SystemConstants.*;

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

    @Autowired
    private UserMapper service;

    @Override
    public Result sendCode(String phone, HttpSession session) {
//        验证手机号是否合法
        if (RegexUtils.isPhoneInvalid(phone))
            return Result.fail("手机号格式有误");

//        生成验证码
        String code = RandomUtil.randomNumbers(4);

//        储存进session
        session.setAttribute(VERIFICATION_CODE, code);

//        发送验证码
        log.info("The verification code is: {}", code);

//        返回
        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO dto, HttpSession session) {
//        校验手机号
        String phone = dto.getPhone();
        if (RegexUtils.isPhoneInvalid(phone))
            return Result.fail("手机号格式有误");
//校验验证码
        String cacheCode = (String) session.getAttribute(VERIFICATION_CODE);
        String code = dto.getCode();
        if (cacheCode == null || !cacheCode.equals(code))
            return Result.fail("验证码有误");

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User user = service.selectOne(wrapper);

//        校验密码
//        String password=dto.getPassword();
//
//        if((!user.getPassword().equals(password))&&password!=null)
//            return Result.fail("密码错误");

        //        判断用户是否存在，不存在则注册
        if(user==null){
            user=new User();
            user.setPhone(phone);
            user.setNickName(USER_NICK_NAME_PREFIX+RandomUtil.randomString(5));
            service.insert(user);
        }
        session.setAttribute(USER,user);
        return Result.ok();
    }

    @Override
    public Result logout() {
        return Result.ok();
    }
}
