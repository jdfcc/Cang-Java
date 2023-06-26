package com.Cang.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.Cang.dto.Result;
import com.Cang.dto.UserDTO;
import com.Cang.entity.Blog;
import com.Cang.service.IBlogService;


import com.Cang.constants.SystemConstants;
import com.Cang.utils.UserHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * @author jdfcc
 * @since 2023-2-7
 */
@RestController
@RequestMapping("/blog")
public class BlogController {

    private final IBlogService blogService;

    public BlogController(IBlogService blogService) {
        this.blogService = blogService;
    }

    /**
     * 保存博客并推送给粉丝
     * @param blog
     * @return
     */
    @PostMapping
    public Result saveBlog(@RequestBody Blog blog) {
      return blogService.saveBlog(blog);

    }

    @PutMapping("/like/{id}")
    public Result likeBlog(@PathVariable("id") Long id) {
        // 修改点赞数量
        return blogService.like(id);

    }

//    @GetMapping("/of/me")
//    public Result queryMyBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
//        // 获取登录用户
//        UserDTO user = UserHolder.getUser();
//        // 根据用户查询
//        Page<Blog> page = blogService.query()
//                .eq("user_id", user.getId()).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
//        // 获取当前页数据
//        List<Blog> records = page.getRecords();
//        return Result.ok(records);
//    }

    @GetMapping("/of/me")
    public Result queryMyBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        // 获取登录用户
        UserDTO user = UserHolder.getUser();
        // 根据用户查询
        Page<Blog> page = blogService.query()
                .eq("user_id", user.getId()).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        return Result.ok(records);
    }


    @GetMapping("/hot")
    public Result queryHotBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        // 根据用户查询
        return blogService.queryHotblog(current);
    }

    @GetMapping("/{id}")
    public Result queryBlog(@PathVariable("id") String id) {
        return blogService.queryBlog(id);
    }

    @GetMapping("/likes/{id}")
    public Result queryLikes(@PathVariable("id") Long id){
        return blogService.queryLikes(String.valueOf(id));
    }

    @GetMapping("/of/user")
    public Result queryBlogByUserId(
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam("id") Long id) {
        // 根据用户查询
        Page<Blog> page = blogService.query()
                .eq("user_id", id).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        return Result.ok(records);
    }

    @GetMapping("/of/follow")
    public Result queryFollow(@RequestParam("lastId") Long max
            , @RequestParam(value = "offset",defaultValue = "0") Integer offset) {
        return blogService.queryFollow(max, offset);
    }

}
