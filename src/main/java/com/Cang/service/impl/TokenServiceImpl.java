package com.Cang.service.impl;

import com.Cang.entity.DoubleToken;
import com.Cang.mapper.TokenMapper;
import com.Cang.service.TokenService;
import com.Cang.utils.TokenUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description TokenServiceImpl
 * @DateTime 2023/10/18 11:03
 */
@Service
public class TokenServiceImpl extends ServiceImpl<TokenMapper, DoubleToken> implements TokenService {

    @Resource
    TokenMapper tokenMapper;

    /**
     * 创建并保存根据用户id生成的token
     *
     * @param userId 用户id
     * @return 创建好的token
     */
    @Override
    public DoubleToken createAndSaveToken(Long userId) throws Exception {
        // 生成双Token，access和refresh
        String accessToken = TokenUtil.generateToken(userId);
        String refreshToken = TokenUtil.generateRefreshToken(userId);
        DoubleToken doubleToken = new DoubleToken();
        doubleToken.setAccessToken(accessToken);
        doubleToken.setRefreshToken(refreshToken);
        doubleToken.setUserId(userId);
//        查询数据库中是否存在此用户的token，如果存在，删除
        LambdaQueryWrapper<DoubleToken> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DoubleToken::getUserId, userId);
        int count = tokenMapper.selectCount(wrapper);
        if (count > 0) {
           remove(userId);
        }
        add(doubleToken);
        return doubleToken;
    }

    /**
     * 添加token
     *
     * @param token 实体类
     */
    @Override
    public void add(DoubleToken token) {
        tokenMapper.insert(token);
    }


    /**
     * 删除对应用户的token
     *
     * @param userId 用户id
     */
    @Override
    public void remove(Long userId) {
        tokenMapper.delete(new LambdaQueryWrapper<DoubleToken>().eq(DoubleToken::getUserId, userId));
    }
}
