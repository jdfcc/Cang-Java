package com.Cang.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jdfcc
 * @Description ChatDto
 * @DateTime 2023/6/4 18:44
 */
@Data
@NoArgsConstructor
public class ChatDto {

    /**
     * 消息id
     */
    private String id;

    /**
     * 消息发送者的头像
     */
    private String avatar;

    /**
     * 消息
     */
    private String message;
    /**
     * 发送者
     */
    private Long send;
    /**
     * 接收者
     */
    private Long receive;

}
