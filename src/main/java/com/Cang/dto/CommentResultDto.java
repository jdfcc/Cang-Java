package com.Cang.dto;

import com.Cang.entity.Comment;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description CommentResultDto
 * @DateTime 2024/1/9 16:55
 */
@Data
public class CommentResultDto implements Serializable {
    /**
     * 主评论
     */
    private Comment comment;

    /**
     * 追评
     */
    private List<CommentDto> responses;

    public void setResponses(CommentDto response) {
        if (responses == null) {
            this.responses =new ArrayList<CommentDto>(){{
                add( response);
            }};
        } else {
            this.responses.add(response);
        }

    }
}
