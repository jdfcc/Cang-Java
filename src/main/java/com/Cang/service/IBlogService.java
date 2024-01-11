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

    Blog queryBlog(String id);
    List<Blog> queryHotblog(Integer current);

    void like(Long id);

    List<UserDTO> queryLikes(String id);

    Long saveBlog(Blog blog);

    ScrollResult queryFollow(Long max, Integer offset);

    List<String> getIds(Long id);
}
