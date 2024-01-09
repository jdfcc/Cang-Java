package com.Cang.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.Cang.dto.Result;
import com.Cang.dto.UserDTO;
import com.Cang.entity.Blog;
import com.Cang.entity.Follow;
import com.Cang.entity.User;
import com.Cang.mapper.FollowMapper;
import com.Cang.service.IBlogService;
import com.Cang.service.IFollowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.Cang.service.IUserService;
import com.Cang.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.Cang.constants.RedisConstants.FEED_KEY;
import static com.Cang.constants.RedisConstants.FOLLOW_KEY;
import static com.Cang.constants.SystemConstants.NOT_LOGIN;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jdfcc
 * @since 2023-2-11
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {

    private final FollowMapper mapper;
    private final StringRedisTemplate redisTemplate;
    private final IUserService userService;
    @Resource
    private IBlogService blogService;

    public FollowServiceImpl(IUserService userService, StringRedisTemplate redisTemplate, FollowMapper mapper) {
        this.userService = userService;
        this.redisTemplate = redisTemplate;
        this.mapper = mapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result follow(Long id, Boolean isFollow) {
        Long userId = UserHolder.getUser();
        if (userId == null) {
            return Result.fail(NOT_LOGIN);
        }
        Follow follower = new Follow();
        follower.setUserId(userId);
        follower.setFollowUserId(id);
        String key = FOLLOW_KEY + userId;
        if (isFollow) {
            redisTemplate.opsForSet().add(key, String.valueOf(id));
            mapper.insert(follower);
//            关注的时候查询所有博客并加入消息队列以供用户查看
            List<String> ids = blogService.getIds(id);
            for (String s : ids) {
                Blog blog = blogService.getById(s);
                LocalDateTime updateTime = blog.getUpdateTime();
                long score = Timestamp.valueOf(updateTime).getTime();
                redisTemplate.opsForZSet().add(FEED_KEY + userId, s, score);
            }
        } else {
            LambdaQueryWrapper<Follow> followWrapper = new LambdaQueryWrapper<>();
            followWrapper.eq(Follow::getUserId, userId);
            redisTemplate.opsForSet().remove(key, String.valueOf(id));
//            取关时，从当前用户消息队列删除目标的blog id
            List<String> ids = blogService.getIds(id);
            for (String s : ids) {
                redisTemplate.opsForZSet().remove(FEED_KEY + userId, s);
            }
            mapper.delete(followWrapper);
        }
        return Result.ok("操作成功");
    }

    @Override
    public Result isFollow(Long id) {
        Long userId = UserHolder.getUser();

        LambdaQueryWrapper<Follow> followWrapper = new LambdaQueryWrapper();
        followWrapper.eq(Follow::getUserId, userId).eq(Follow::getFollowUserId, id);
        Follow follow = mapper.selectOne(followWrapper);
        if (follow == null) {
            return Result.ok(false);
        }
        return Result.ok(true);
    }

    @Override
    public Result common(Long id) {
        Long userId= UserHolder.getUser();
        Set<String> follow = redisTemplate.opsForSet().intersect(FOLLOW_KEY + userId, FOLLOW_KEY + id);
        List<UserDTO> users = new ArrayList<>();
        if (follow == null) {
            return Result.ok(Collections.emptyList());
        }
        for (String temp : follow) {
            User u = userService.getById(temp);
            UserDTO userDTO = BeanUtil.copyProperties(u, UserDTO.class);
            users.add(userDTO);
        }
        return Result.ok(users);
    }
}
