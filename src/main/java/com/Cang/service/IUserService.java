package com.Cang.service;

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
     * @param session
     * @return
     */
    Result sendCode(String email, HttpSession session);

    /**
     * 用户登录
     * @param dto
     * @param session
     * @return
     */
    Result login(LoginFormDTO dto,HttpSession session) throws Exception;

    /**
     * 用户登出
     * @param request
     * @return
     */

    Result logout(HttpServletRequest request);

    /**
     * 获取用户头像
     * @param userid
     * @return
     */
    Result getAvatar(Long userid);
}
