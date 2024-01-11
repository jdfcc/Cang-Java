package com.Cang.dto;

import com.Cang.entity.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description CommentDto
 * @DateTime 2024/1/9 16:22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommentDto extends Comment implements Serializable {
    private String sendNickname;
    private String sendAvatar;
    private String replayNickname;
    private String replayAvatar;
}
