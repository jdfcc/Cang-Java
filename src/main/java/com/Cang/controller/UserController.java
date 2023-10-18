package com.Cang.controller;


import cn.hutool.core.bean.BeanUtil;
import com.Cang.dto.LoginFormDTO;
import com.Cang.dto.Result;
import com.Cang.dto.UserDTO;
import com.Cang.entity.User;
import com.Cang.entity.UserInfo;
import com.Cang.service.IUserInfoService;
import com.Cang.service.IUserService;
import com.Cang.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * @author jdfcc
 * @since 2023-1-3
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    @Resource
    private IUserInfoService userInfoService;

    /**
     * 发送手机验证码
     */
    @PostMapping("code")
    public Result sendCode(@RequestParam("phone") String phone, HttpSession session) {

        return userService.sendCode(phone, session);
    }


    /**
     * 登录功能
     *
     * @param loginForm 登录参数，包含手机号、验证码；或者手机号、密码
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginFormDTO loginForm, HttpSession session) throws Exception {
        return userService.login(loginForm, session);
    }

    /**
     * 登出功能
     *
     * @return 无
     */
    @PostMapping("/logout")
    public Result logout(HttpServletRequest request) {
        //  实现登出功能
        return userService.logout(request);
    }

    @GetMapping("/me")
    public Result me() {
        //  获取当前登录的用户并返回
        UserDTO user = UserHolder.getUser();
        log.info("User {}", user);
        return Result.ok(user);
    }

    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long userId) {
        // 查询详情
        UserInfo info = userInfoService.getById(String.valueOf(userId));
// 没有详情，应该是第一次查看详情
        if (info == null) {
            UserInfo userInfo = new UserInfo();
            User user = userService.getById(userId);
            log.debug("id&&&&&&&&&&&&&&&& {} {}", userId, user.getId());
            userInfo.setId(user.getId());
            userInfo.setCreateTime(user.getCreateTime());
            userInfo.setUpdateTime(user.getUpdateTime());
            userInfoService.save(userInfo);
            return Result.ok(userInfo);
        }
        // 返回
        return Result.ok(info);
    }

    @GetMapping("/{id}")
    public Result queryUserById(@PathVariable("id") Long userId) {
        // 查询详情
        User user = userService.getById(userId);
        if (user == null) {
            return Result.ok();
        }
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        // 返回
        return Result.ok(userDTO);
    }
}
