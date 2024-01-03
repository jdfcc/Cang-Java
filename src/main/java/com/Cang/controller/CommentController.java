package com.Cang.controller;

import com.Cang.dto.Result;
import com.Cang.entity.Comment;
import com.Cang.service.CommentService;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

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

}
