package com.Cang.service;

import com.Cang.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description CommentService
 * @DateTime 2024/1/3 10:27
 */
public interface CommentService extends IService<Comment> {

    /**
     * 根据postID查找其所属的所有评论
     * @param postId postId
     * @return List<Comment>
     */
     List<Comment>  listComments(Long postId);

    /**
     * 保存评论
     * @param comment commentEntity
     */
    void saveComment(Comment comment);
}
