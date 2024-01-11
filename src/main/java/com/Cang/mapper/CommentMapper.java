package com.Cang.mapper;

import com.Cang.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description CommentMapper
 * @DateTime 2024/1/3 10:26
 */
public interface CommentMapper extends BaseMapper<Comment> {
    List<Comment> page(Long postId,Integer index,Integer size);

}
