package com.Cang.controller;

import com.Cang.dto.Result;
import com.Cang.entity.Comment;
import com.Cang.service.CommentService;
import com.Cang.utils.UserHolder;
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

    @PostMapping("/send")
    Result comment(@RequestBody @Valid Comment comment) {
        if (!comment.getId().equals(UserHolder.getUser())) {
            return Result.fail("当前id与当前用户不是同一用户");
        }
        commentService.saveComment(comment);
        return Result.ok();
    }

    /**
     * 根据PostId获取此资源下的所有评论
     *
     * @return Result
     */
    @GetMapping("/list/{postId}")
    Result list(@PathVariable Long postId) {
        return Result.ok(commentService.listComments(postId));
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
    @DeleteMapping("/del/commentId/{commentId}/send/{send}")
    Result del(@PathVariable("commentId") String commentId, @PathVariable("send") String send) {
        if (!Long.valueOf(send).equals(UserHolder.getUser())) {
            return Result.fail("当前id与当前用户不是同一用户");
        }
        commentService.deleteComment(Long.valueOf(commentId));
        return Result.ok();
    }


}
