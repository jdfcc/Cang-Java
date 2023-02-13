package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmdp.dto.Result;
import com.hmdp.dto.ScrollResult;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.Blog;
import com.hmdp.entity.User;
import com.hmdp.mapper.BlogMapper;
import com.hmdp.mapper.FollowMapper;
import com.hmdp.service.IBlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.service.IFollowService;
import com.hmdp.service.IUserService;
import com.hmdp.utils.CacheClient;
import com.hmdp.utils.SystemConstants;
import com.hmdp.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.hmdp.utils.RedisConstants.*;
import static com.hmdp.utils.SystemConstants.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jdfcc
 * @since 2023-2-11
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {
    @Resource
    private IUserService userService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Resource
    private BlogMapper blogMapper;
    @Resource
    private CacheClient cacheClient;
    @Resource
    private FollowMapper followMapper;

    @Override
    public Result queryHotblog(Integer current) {
        String json = redisTemplate.opsForValue().get(BLOG_HOT_KEY);
        List<Blog> lists = JSONUtil.toList(json, Blog.class);
        if (!lists.isEmpty())
            return Result.ok(lists);
        Page<Blog> page = this.query()
                .orderByDesc("liked")
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));

//        Page page = cacheClient.queryWithCacheThrough(
//                BLOG_HOT_KEY, null, Page.class, this::queryPage, BLOG_HOT_KEY_TTL, TimeUnit.SECONDS);
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        // 查询用户
        records.forEach(blog -> {
            Long userId = blog.getUserId();
            this.queryBlogUser(blog);
            String key = BLOG_LIKED_KEY + blog.getId();
            Boolean liked = this.isLiked(key, userId);
            blog.setIsLike(liked);
        });
        cacheClient.set(BLOG_HOT_KEY, records, BLOG_HOT_KEY_TTL, TimeUnit.SECONDS);
        return Result.ok(records);

    }

    @Override
    public Result queryFollow(Long max, Integer offset) {
        UserDTO user = UserHolder.getUser();
        if (user == null)
            return Result.fail(NOT_LOGIN);
        String key = FEED_KEY + user.getId();
        Set<ZSetOperations.TypedTuple<String>> typedTuples = redisTemplate.
                opsForZSet().rangeByScoreWithScores(key, 0, max, offset, 2);
        if (typedTuples == null || typedTuples.isEmpty())
            return Result.ok();
        List<Long> ids = new ArrayList<>(typedTuples.size());
        int count = 1;//偏移量
        Long min_score = 0L;//最小值
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            ids.add(Long.valueOf(typedTuple.getValue()));
            Long score = typedTuple.getScore().longValue();
            if (score == min_score) {
                count++;
            } else {
                min_score = score;
                count = 1;
            }
        }
        List<Blog> blogs = new ArrayList<>();
        for (Long id : ids) {
            Blog blog = blogMapper.selectById(id);
            blogs.add(blog);
        }
        for (Blog blog : blogs) {
            queryBlogUser(blog);
            Boolean liked = this.isLiked(key, user.getId());
            blog.setIsLike(liked);
        }
        ScrollResult r = new ScrollResult();
        r.setList(blogs);
        r.setMinTime(min_score);
        r.setOffset(count);
        return Result.ok(r);
    }

    public List<String> getIds(Long id) {
        return blogMapper.getIds(String.valueOf(id));
    }

    /**
     * 当用户发布博客时，查询关注此用户的所有粉丝并加入set集合以将此blog推送给他们
     *
     * @param blog
     * @return
     */
    @Transactional
    @Override
    public Result saveBlog(Blog blog) {
        UserDTO user = UserHolder.getUser();
        blog.setUserId(user.getId());
        // 保存探店博文
        int insert = blogMapper.insert(blog);
        if (insert == 0)
            return Result.fail(CREATE_BLOG_FAILED);
        List<String> followsId = followMapper.getFollowerId(user.getId());
        for (String followId : followsId) {
            String key = FEED_KEY + followId;
            redisTemplate.opsForZSet()
                    .add(key, String.valueOf(blog.getId()), System.currentTimeMillis());
        }
        return Result.ok(blog.getId());
    }

    @Override
    public Result queryLikes(String id) {

        String key = BLOG_LIKED_KEY + id;
        // 1.查询top5的点赞用户 zrange key 0 4
        Set<String> top5 = redisTemplate.opsForZSet().range(key, 0, 4);
        if (top5 == null || top5.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }
        // 2.解析出其中的用户id
        List<Long> ids = top5.stream().map(Long::valueOf).collect(Collectors.toList());
        String idStr = StrUtil.join(",", ids);
        // 3.根据用户id查询用户 WHERE id IN ( 5 , 1 ) ORDER BY FIELD(id, 5, 1)
        List<UserDTO> userDTOS = userService.query()
                .in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list()
                .stream()
                .map(user -> BeanUtil.copyProperties(user, UserDTO.class))
                .collect(Collectors.toList());
        // 4.返回
        return Result.ok(userDTOS);
    }

    @Transactional
    @Override
    public Result like(Long id) {
        UserDTO user = UserHolder.getUser();
        if (user == null)
            return Result.fail(NOT_LOGIN);
        Long userId = user.getId();
        String key = BLOG_LIKED_KEY + id;
//        查询是否点赞
        Boolean isMember = this.isLiked(key, userId);
//        已点赞，取消点赞,从set集合里面移除用户，点赞数减一
        if (isMember) {
            Boolean unliked = blogMapper.unliked(id);
            if (unliked)//移除成功
                redisTemplate.opsForZSet().remove(key, String.valueOf(userId));
        } else {//未点赞,点赞数加一，将用户添加至集合
            Boolean liked = blogMapper.liked(id);
            if (liked)
                redisTemplate.opsForZSet().add(key, String.valueOf(userId), System.currentTimeMillis());
        }
        return Result.ok();
    }


    @Override
    public Result queryBlog(String id) {
        String json = redisTemplate.opsForValue().get(BLOG_KEY + id);
        Blog b = JSONUtil.toBean(json, Blog.class);
        if (!(b.getId() == null))
            return Result.ok(b);
        Blog blog = this.getById(id);
        String key = BLOG_LIKED_KEY + id;
        if (blog == null)
            return Result.fail("blog exists");
        Long userId = blog.getUserId();
        User user = userService.getById(userId);
        blog.setName(user.getNickName());
        blog.setIcon(user.getIcon());
        blog.setIsLike(this.isLiked(key, userId));
        cacheClient.set(BLOG_KEY + id, blog, BLOG_KEY_TTL, TimeUnit.SECONDS);
        return Result.ok(blog);
    }

    public Boolean isLiked(String key, Long userId) {
        Double score = redisTemplate.opsForZSet().score(key, String.valueOf(userId));
        if (score == null)
            return false;
        return true;
    }

    private void queryBlogUser(Blog blog) {
        Long userId = blog.getUserId();
        User user = userService.getById(userId);
        blog.setName(user.getNickName());
        blog.setIcon(user.getIcon());
    }
}
