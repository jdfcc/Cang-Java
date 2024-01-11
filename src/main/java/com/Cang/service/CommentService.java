package com.Cang.service;

import com.Cang.dto.CommentDto;
import com.Cang.dto.CommentResultDto;
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
     * @return List<CommentResultDto>
     */
    List<CommentResultDto>  listComments(Long postId,Integer index,Integer limit);

    /**
     * 获取此评论下的所有评论
     * @param commentId 此评论id
     * @return List<CommentDto>
     */
    List<CommentDto> getAllComments(Long commentId);


    /**
     * 获取当前用户的所有评论
     */
     List<Comment> getMyComment(Long userId);

     /**
     * 保存评论
     * @param comment commentEntity
     */
    void saveComment(Comment comment);

    /**
     * 根据评论id删除评论
     * @param commentId 评论id
     */
    void deleteComment(Long  commentId);



}
