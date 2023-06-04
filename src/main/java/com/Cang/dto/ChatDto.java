package com.Cang.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Jdfcc
 * @Description 将Chat封装返回给聊天用户的实体类
 * @DateTime 2023/6/3 19:29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatDto {

    /**
     * 防止精度丢失
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;


    /**
     * 发送此条用户的用户头像
     */
    private String avatar;


    /**
     * 消息类型
     */
    private String type;

    private String nickName;

    /**
     * 发送者
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long send;

    /**
     * 接收者
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long receive;

    /**
     * 消息实体
     */
    private String message;


    public ChatDto(String type, String message) {
        this.type = type;
        this.message = message;
    }
}
