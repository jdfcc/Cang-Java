package com.Cang.service;

import com.Cang.entity.DoubleToken;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description TokenService
 * @DateTime 2023/10/18 11:02
 */
public interface TokenService extends IService<DoubleToken> {

    /**
     * 创建并保存根据用户id生成的token
     *
     * @param userId 用户id
     * @return 创建好的token
     */
    DoubleToken createAndSaveToken(Long userId) throws Exception;


    /**
     * 添加token
     *
     * @param token 实体类
     */
    void add(DoubleToken token);

    /**
     * 刷新accessToken
     *
     * @param refreshToken 刷新令牌
     */
    String refreshAccessToken(Long userid, String refreshToken) throws Exception;


    /**
     * 删除对应用户的token
     *
     * @param userId 用户id
     */
    void remove(Long userId);
}
