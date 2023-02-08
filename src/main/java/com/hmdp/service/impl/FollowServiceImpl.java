package com.hmdp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.Follow;
import com.hmdp.mapper.FollowMapper;
import com.hmdp.service.IFollowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.CacheClient;
import com.hmdp.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static com.hmdp.utils.SystemConstants.NOT_LOGIN;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {

    @Autowired
    private FollowMapper mapper;

    @Transactional
    @Override
    public Result follow(Long id, Boolean isFollow) {
        UserDTO user= UserHolder.getUser();
        if(user==null)
            return Result.fail(NOT_LOGIN);
        Long userId= user.getId();
        Follow follower=new Follow();
        follower.setUserId(userId);
        follower.setFollowUserId(id);
        if(isFollow)
            mapper.insert(follower);
        else {
            LambdaQueryWrapper<Follow> followWrapper=new LambdaQueryWrapper();
            followWrapper.eq(Follow::getUserId,userId);
            mapper.delete(followWrapper);
        }
        return Result.ok("操作成功");
    }

    @Override
    public Result isFollow(Long id) {
        UserDTO user= UserHolder.getUser();
        if(user==null)
            return Result.fail(NOT_LOGIN);
        Long userId= user.getId();
        LambdaQueryWrapper<Follow> followWrapper= new LambdaQueryWrapper();
        followWrapper.eq(Follow::getUserId,userId).eq(Follow::getFollowUserId,id);
        Follow follow = mapper.selectOne(followWrapper);
        if(follow==null)
            return Result.ok(false);
        return Result.ok(true);
    }
}
