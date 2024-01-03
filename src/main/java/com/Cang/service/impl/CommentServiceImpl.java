package com.Cang.service.impl;

import com.Cang.entity.Comment;
import com.Cang.mapper.CommentMapper;
import com.Cang.service.CommentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description CommentServiceImpl
 * @DateTime 2024/1/3 10:28
 */
@Service
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    /**
     * 保存评论
     *
     * @param comment commentEntity
     */
    @Override
    public void saveComment(Comment comment) {
        save(comment);
    }

    /**
     * 根据postID查找其所属的所有评论
     *
     * @param postId postId
     * @return List<Comment>
     */
    @Override
    public List<Comment> listComments(Long postId) {
        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commentLambdaQueryWrapper.eq(Comment::getPostId, postId);
        commentLambdaQueryWrapper.orderByDesc(Comment::getCreateTime);
        return list(commentLambdaQueryWrapper);
    }

}
