package com.Cang.service;

import com.Cang.dto.UserDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.Cang.dto.LoginFormDTO;
import com.Cang.dto.Result;
import com.Cang.entity.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jdfcc
 * @since 2022-12-22
 */
public interface IUserService extends IService<User> {

    /**
     * 用户注册或登录发送验证码
     * @param email 邮箱
     */
    void sendCode(String email);

    /**
     * 用户登录
     */
    Result login(LoginFormDTO dto,HttpSession session) throws Exception;

    /**
     * 用户登出
     */

    Result logout(HttpServletRequest request);

    /**
     * 获取用户头像
     * @param userid 用户昵称
     * @return 用户id
     */
    String getAvatar(Long userid);

    /**
     * 获取用户信息
     * @param userid 用户id
     * @return 用户信息
     */
    UserDTO getUserInfo(Long userid);

}
