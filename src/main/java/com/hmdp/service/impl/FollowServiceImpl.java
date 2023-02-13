package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.Follow;
import com.hmdp.entity.User;
import com.hmdp.mapper.FollowMapper;
import com.hmdp.service.IFollowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.service.IUserService;
import com.hmdp.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.hmdp.utils.RedisConstants.FOLLOW_KEY;
import static com.hmdp.utils.SystemConstants.NOT_LOGIN;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jdfcc
 * @since 2023-2-11
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {

    @Autowired
    private FollowMapper mapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private IUserService userService;

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
        String key=FOLLOW_KEY+userId;
        if(isFollow){
            redisTemplate.opsForSet().add(key, String.valueOf(id));
            mapper.insert(follower);
        }
        else {
            LambdaQueryWrapper<Follow> followWrapper=new LambdaQueryWrapper();
            followWrapper.eq(Follow::getUserId,userId);
            redisTemplate.opsForSet().remove(key, String.valueOf(id));
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

    @Override
    public Result common(Long id) {
        UserDTO user= UserHolder.getUser();
        if(user==null)
            return Result.fail(NOT_LOGIN);
        Long userId= user.getId();
        Set<String> follow = redisTemplate.opsForSet().intersect(FOLLOW_KEY + userId, FOLLOW_KEY + id);
        List<UserDTO> users = new ArrayList<>();
        if(follow==null)
            return Result.ok(Collections.emptyList());
        for(String temp:follow)
        {
            User u = userService.getById(temp);
            UserDTO userDTO = BeanUtil.copyProperties(u, UserDTO.class);
            users.add(userDTO);
        }
        return Result.ok(users);
    }
}
