package com.hmdp.service;

import com.hmdp.dto.Result;
import com.hmdp.entity.Blog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author jdfcc
 * @since 2023-2-12
 */
public interface IBlogService extends IService<Blog> {

    Result queryBlog(String id);


    Result queryHotblog(Integer current);

    Result like(Long id);


    Result queryLikes(String id);

    Result saveBlog(Blog blog);

    Result queryFollow(Long max, Integer offset);
}
