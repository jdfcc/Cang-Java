package com.Cang.controller;

import com.Cang.dto.Result;
import com.Cang.entity.Comment;
import com.Cang.service.CommentService;
import com.Cang.utils.UserHolder;
import org.apache.ibatis.annotations.Param;
import org.assertj.core.data.Index;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description CommentController
 * @DateTime 2024/1/3 10:31
 */
@RequestMapping("/comment")
@RestController
public class CommentController {
    @Resource
    private CommentService commentService;

    /**
     * 发送评论
     *
     * @param comment 评论实体
     * @return Result
     */
    @PostMapping("/send")
    Result comment(@RequestBody @Valid Comment comment) {

        Comment newComment = commentService.saveComment(comment);
        return Result.ok(newComment);
    }

    /**
     * 根据PostId获取此资源下的所有评论，默认值为20
     *
     * @return Result
     */
    @GetMapping("/list/{postId}")
    Result list(@PathVariable String postId, @RequestParam(value = "index", defaultValue = "1") Integer index) {
        return Result.ok(commentService.listComments(Long.valueOf(postId), index, 20));
    }

    /**
     * 查询当前用户的所有评论
     *
     * @return Result
     */
    @GetMapping("/ofMe")
    Result myComment() {
        return Result.ok(commentService.getMyComment(UserHolder.getUser()));
    }


    /**
     * 删除评论
     */
    @DeleteMapping("/del/commentId/{commentId}")
    Result del(@PathVariable("commentId") String commentId) {
        commentService.deleteComment(Long.valueOf(commentId));
        return Result.ok();
    }

    /**
     * 查看全部回复
     *
     * @param commentId 评论id
     * @return 此评论下的所有回复
     */
    @GetMapping("/showAll/{commentId}}")
    Result showAllComments(@PathVariable("commentId") String commentId) {
        return Result.ok(commentService.getAllComments(Long.valueOf(commentId)));
    }


}
