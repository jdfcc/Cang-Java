package com.Cang.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description Comment
 * @DateTime 2024/1/3 9:52
 */
@TableName("comment")
@Data
public class Comment implements Serializable {

    @JsonSerialize
    private Long id;
    private Long send;
    private Long reply;
    private Long parent;
    @NotNull(message = "发送的内容不能为空")
    @NotEmpty(message = "发送的内容不能为空")
    private String content;

    private Boolean liked;

    private Long postId;


    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评分
     */
    private Integer rate;

    @JsonIgnore
    private Integer status;
    @JsonIgnore
    private LocalDateTime createTime;
}
