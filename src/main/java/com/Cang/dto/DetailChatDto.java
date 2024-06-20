package com.Cang.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description 私聊页面消息封装
 * @DateTime 2024/5/25 22:50
 */
public class DetailChatDto extends ChatDto implements Serializable {
    /**
     * 我的头像
     */
    private String myAvatar;
    /**
     * 我的昵称
     */
    private String myName;
}
