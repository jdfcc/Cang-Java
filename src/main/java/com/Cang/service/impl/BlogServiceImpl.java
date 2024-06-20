package com.Cang.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.Cang.dto.ScrollResult;
import com.Cang.dto.UserDTO;
import com.Cang.entity.Blog;
import com.Cang.entity.User;
import com.Cang.mapper.BlogMapper;
import com.Cang.mapper.FollowMapper;
import com.Cang.service.IBlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.Cang.service.IUserService;
import com.Cang.utils.CacheClient;
import com.Cang.constants.SystemConstants;
import com.Cang.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.Cang.constants.RedisConstants.*;

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
    private final IUserService userService;
    private final StringRedisTemplate redisTemplate;
    private final BlogMapper blogMapper;
    @Resource
    private CacheClient cacheClient;
    private final FollowMapper followMapper;


    public BlogServiceImpl(StringRedisTemplate redisTemplate, IUserService userService, BlogMapper blogMapper, FollowMapper followMapper) {
        this.redisTemplate = redisTemplate;
        this.userService = userService;
        this.blogMapper = blogMapper;
        this.followMapper = followMapper;
    }

    @Override
//    @LogAnnotation
    public List<Blog> queryHotblog(Integer current) {
        String json = redisTemplate.opsForValue().get(BLOG_HOT_KEY+ current);
        List<Blog> lists = JSONUtil.toList(json, Blog.class);
        if (!lists.isEmpty()) {
            return lists;
        }
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
        cacheClient.set(BLOG_HOT_KEY+current, records, BLOG_HOT_KEY_TTL, TimeUnit.SECONDS);
        return records;
    }

    @Override
    public Blog queryBlog(String id) {
        String json = redisTemplate.opsForValue().get(BLOG_KEY + id);
        Blog b = JSONUtil.toBean(json, Blog.class);
        if (!(b.getId() == null)) {
            return b;
        }
        Blog blog = this.getById(id);
        String key = BLOG_LIKED_KEY + id;
        if (blog == null) {
            throw new NullPointerException("blog is null");
        }
        Long userId = blog.getUserId();
        User user = userService.getById(userId);
        blog.setName(user.getNickName());
        blog.setIcon(user.getIcon());
        blog.setIsLike(this.isLiked(key, userId));
        cacheClient.set(BLOG_KEY + id, blog, BLOG_KEY_TTL, TimeUnit.SECONDS);
        return blog;
    }

    @Override
    public ScrollResult queryFollow(Long max, Integer offset) {
//        UserDTO user = UserHolder.getUser();
//        if (user == null)
//            return Result.fail(NOT_LOGIN);
//        String key = FEED_KEY + user.getId();
//        Set<ZSetOperations.TypedTuple<String>> typedTuples = redisTemplate.
//                opsForZSet().rangeByScoreWithScores(key, 0, max, offset, 2);
//        if (typedTuples == null || typedTuples.isEmpty())
//            return Result.ok();
//        List<Long> ids = new ArrayList<>(typedTuples.size());
//        int count = 1;//偏移量
//        Long min_score = 0L;//最小值
//        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
//            ids.add(Long.valueOf(typedTuple.getValue()));
//            Long score = typedTuple.getScore().longValue();
//            if (score == min_score) {
//                count++;
//            } else {
//                min_score = score;
//                count = 1;
//            }
//        }
//        List<Blog> blogs = new ArrayList<>();
//        for (Long id : ids) {
//            Blog blog = blogMapper.selectById(id);
//            blogs.add(blog);
//        }
//        for (Blog blog : blogs) {
//            queryBlogUser(blog);
//            Boolean liked = this.isLiked(key, user.getId());
//            blog.setIsLike(liked);
//        }
//        ScrollResult r = new ScrollResult();
//        r.setList(blogs);
//        r.setMinTime(min_score);
//        r.setOffset(count);
//        return Result.ok(r);
        // 1.获取当前用户

        Long userId = UserHolder.getUser();
        // 2.查询收件箱 ZREVRANGEBYSCORE key Max Min LIMIT offset count
        String key = FEED_KEY + userId;
        Set<ZSetOperations.TypedTuple<String>> typedTuples = redisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(key, 0, max, offset, 2);

        // 3.非空判断
        if (typedTuples == null || typedTuples.isEmpty()) {
            return new ScrollResult();
        }
        // 4.解析数据：blogId、minTime（时间戳）、offset
        List<Long> ids = new ArrayList<>(typedTuples.size());
        long minTime = 0; // 2
        int os = 1; // 2
        for (ZSetOperations.TypedTuple<String> tuple : typedTuples) { // 5 4 4 2 2
            // 4.1.获取id
            ids.add(Long.valueOf(Objects.requireNonNull(tuple.getValue())));
            // 4.2.获取分数(时间戳）
            long time = Objects.requireNonNull(tuple.getScore()).longValue();
            if (time == minTime) {
                os++;
            } else {
                minTime = time;
                os = 1;
            }
        }

        // 5.根据id查询blog
        String idStr = StrUtil.join(",", ids);
        List<Blog> blogs = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();

        for (Blog blog : blogs) {
            // 5.1.查询blog有关的用户
            queryBlogUser(blog);
            // 5.2.查询blog是否被点赞
            Boolean liked = this.isLiked(key, userId);
            blog.setIsLike(liked);
        }

        // 6.封装并返回
        ScrollResult r = new ScrollResult();
        r.setList(blogs);
        r.setOffset(os);
        r.setMinTime(minTime);
        return r;
    }

    /**
     * @return List<Blog>
     */
    @Override
    public List<Blog> getMyBlogs() {
      return this.showBlogs(UserHolder.getUser());
    }

    /**
     * 查询此用户下的所有博客
     *
     * @param userId 用户id
     * @return 博客集合
     */
    @Override
    public List<Blog> showBlogs(Long userId) {
        LambdaQueryWrapper<Blog> blogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        blogLambdaQueryWrapper.eq(Blog::getUserId, userId);
        return list(blogLambdaQueryWrapper);    }

    @Override
    public List<String> getIds(Long id) {
        return blogMapper.getIds(String.valueOf(id));
    }

    /**
     * 当用户发布博客时，查询关注此用户的所有粉丝并加入set集合以将此blog推送给他们
     */
    @Transactional
    @Override
    public Long saveBlog(Blog blog) {
        Long userId = UserHolder.getUser();
        blog.setUserId(userId);
        // 保存帖子
        blogMapper.insert(blog);

        List<String> followsId = followMapper.getFollowerId(userId);
        for (String followId : followsId) {
            String key = FEED_KEY + followId;
//            发布消息
            redisTemplate.opsForZSet()
                    .add(key, String.valueOf(blog.getId()), System.currentTimeMillis());
        }
        return blog.getId();
    }

    @Override
    public List<UserDTO> queryLikes(String id) {

        String key = BLOG_LIKED_KEY + id;
        // 1.查询top5的点赞用户 zrange key 0 4
        Set<String> top5 = redisTemplate.opsForZSet().range(key, 0, 4);
        if (top5 == null || top5.isEmpty()) {
            return Collections.emptyList();
        }
        // 2.解析出其中的用户id
        List<Long> ids = top5.stream().map(Long::valueOf).collect(Collectors.toList());
        String idStr = StrUtil.join(",", ids);
        // 3.根据用户id查询用户 WHERE id IN ( 5 , 1 ) ORDER BY FIELD(id, 5, 1)
        // 4.返回
        return userService.query()
                .in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list()
                .stream()
                .map(user -> BeanUtil.copyProperties(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void like(Long id) {
        Long userId = UserHolder.getUser();
        String key = BLOG_LIKED_KEY + id;
//        查询是否点赞
        Boolean isMember = this.isLiked(key, userId);
//        已点赞，取消点赞,从set集合里面移除用户，点赞数减一
        if (isMember) {

            Boolean unliked = blogMapper.unliked(id);
            if (unliked)//移除成功
            {
                redisTemplate.opsForZSet().remove(key, String.valueOf(userId));
                redisTemplate.opsForValue().getAndDelete(BLOG_HOT_KEY);
            }

        } else {//未点赞,点赞数加一，将用户添加至集合
            Boolean liked = blogMapper.liked(id);

            if (liked) {
                redisTemplate.opsForZSet().add(key, String.valueOf(userId), System.currentTimeMillis());
            }
        }
        redisTemplate.opsForValue().getAndDelete(BLOG_KEY + id);
        redisTemplate.opsForValue().getAndDelete(BLOG_HOT_KEY);
    }


    /**
     * 查询当前用户是否点赞
     *
     * @param key    前缀
     * @param userId 要查询的用户id
     * @return 是否点赞
     */
    public Boolean isLiked(String key, Long userId) {
        Double score = redisTemplate.opsForZSet().score(key, String.valueOf(userId));
        return score != null;
    }

    private void queryBlogUser(Blog blog) {
        Long userId = blog.getUserId();
        User user = userService.getById(userId);
        blog.setName(user.getNickName());
        blog.setIcon(user.getIcon());
    }
}
