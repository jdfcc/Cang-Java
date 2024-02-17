package com.Cang.service;

import com.Cang.annotations.LogAnnotation;
import com.Cang.dto.Result;
import com.Cang.dto.ScrollResult;
import com.Cang.dto.UserDTO;
import com.Cang.entity.Blog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author jdfcc
 * @since 2023-2-12
 */
public interface IBlogService extends IService<Blog> {

    /**
     * 通过博客id查询博客
     * @param id 博客id
     * @return blog
     */
    Blog queryBlog(String id);
    List<Blog> queryHotblog(Integer current);

    void like(Long id);

    List<UserDTO> queryLikes(String id);

    Long saveBlog(Blog blog);

    ScrollResult queryFollow(Long max, Integer offset);

    /**
     * 获取此用户id下的所有博客id
     * @param id 用户id
     * @return 所有的博客id
     */
    List<String> getIds(Long id);

    List<Blog> getMyBlogs();

    /**
     * 查询此用户下的所有博客
     * @param userId 用户id
     * @return 博客集合
     */
    List<Blog> showBlogs(Long userId);
}
