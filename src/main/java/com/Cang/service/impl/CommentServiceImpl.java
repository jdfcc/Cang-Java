package com.Cang.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.Cang.dto.*;
import com.Cang.entity.Comment;
import com.Cang.entity.User;
import com.Cang.mapper.CommentMapper;
import com.Cang.service.CommentService;
import com.Cang.service.IUserService;
import com.Cang.utils.UserHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
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
    @Resource
    private IUserService userService;

    @Resource
    private CommentMapper commentMapper;


    /**
     * 根据评论id删除评论
     *
     * @param commentId 评论id
     */
    @Override
    public void deleteComment(Long commentId) {
        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commentLambdaQueryWrapper.eq(Comment::getId, commentId);
        remove(commentLambdaQueryWrapper);
    }


    /**
     * 保存评论
     *
     * @param comment commentEntity
     */
    @Override
    public void saveComment(Comment comment) {
        save(comment);
    }


    private CommentDto handleComment(Comment comment) {
        // 封装处理单条评论
        Long sender = comment.getSend();
        Long replayed = comment.getReply();
        UserDTO sendInfo = userService.getUserInfo(sender);
        UserDTO repInfo = userService.getUserInfo(replayed);
        CommentDto commentDto = new CommentDto();
        BeanUtil.copyProperties(comment, commentDto);
        commentDto.setSendAvatar(sendInfo.getIcon());
        commentDto.setSendNickname(sendInfo.getNickName());
        commentDto.setReplayAvatar(repInfo.getIcon());
        commentDto.setReplayNickname(repInfo.getNickName());
        return commentDto;
    }

    /**
     * 获取此评论下的所有评论
     *
     * @param commentId 此评论id
     * @return List<CommentDto>
     */
    @Override
    public List<CommentDto> getAllComments(Long commentId) {
        List<Comment> comments = selectComments(commentId);
        ArrayList<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            CommentDto commentDto = handleComment(comment);
            commentDtos.add(commentDto);
        }
        return commentDtos;
    }

    /**
     * 查找此id下的所有追评,点赞倒序
     *
     * @param commentId 评论id
     * @return List<Comment>
     */
    List<Comment> selectComments(Long commentId) {
        LambdaQueryWrapper<Comment> dtoWrapper = new LambdaQueryWrapper<>();
        dtoWrapper.eq(Comment::getParent, commentId).orderByDesc(Comment::getLikeCount);
        return list(dtoWrapper);
    }

    /**
     * 根据postID查找其所属的所有评论
     *
     * @param postId postId
     * @return List<Comment>
     */
    @Override
    public List<CommentResultDto> listComments(Long postId, Integer index, Integer limit) {
        index = index > 1 ? (index - 1) * limit : 0;
        List<Comment> list = commentMapper.page(postId, index, limit);
        if (list == null) {
            return new ArrayList<>();
        }
        ArrayList<CommentResultDto> commentResultDtos = new ArrayList<>();
        // 处理追评的评论

        for (Comment comment : list) {
            CommentResultDto commentResultDto = new CommentResultDto();
            // 封装处理单条评论,为此评论赋予头像和昵称
            CommentDto mainComment = handleComment(comment);
            commentResultDto.setComment(mainComment);

            // 获取此资源下每条评论下两条点赞数最高的评论
            Long id = comment.getId();
            List<Comment> res = selectComments(id);
            if (CollectionUtil.isEmpty(res)) {
                commentResultDto.setResponses(new CommentDto());
            } else {
                // 为此评论下的两条评论封装头像和昵称
                int size = Math.min(res.size(), 2);
                for (Comment re : res.subList(0, size)) {
                    CommentDto commentDto = handleComment(re);
                    commentResultDto.setResponses(commentDto);
                }
            }
            commentResultDtos.add(commentResultDto);
        }
        return commentResultDtos;
    }

    /**
     * 获取当前用户的所有评论
     *
     * @param userId 用户id
     */
    @Override
    public List<Comment> getMyComment(Long userId) {
        return list(new LambdaQueryWrapper<Comment>() {{
            eq(Comment::getSend, userId);
        }});
    }
}
