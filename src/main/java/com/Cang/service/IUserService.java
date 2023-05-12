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
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IUserService extends IService<User> {

    Result sendCode(String phone, HttpSession session);

    Result login(LoginFormDTO dto,HttpSession session);

    Result logout(HttpServletRequest request);
}
