package com.Cang.dto;


import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
@ToString
public class MessageDto {

    /**
     * 目标用户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long targetId;

    /**
     * 发送者id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userid;

    /**
     * 消息类型
     */
    private String type;

    /**
     * 消息队列
     */
    Object data;


    public MessageDto(Long targetId, String type, Object data) {
        this.targetId = targetId;
        this.type = type;
        this.data = data;
    }


    public static String heartBeat() {
        return JSON.toJSONString(new MessageDto(null, "heartbeat", null));
    }

    public static String message(Long targetId, Object data) {
        return JSON.toJSONString(new MessageDto(targetId, "message", data));
    }

    public static String query(Long targetId, Object data) {
        return JSON.toJSONString(new MessageDto(targetId, "query", data));
    }

    public static String list(Long userId, Object data) {
        return JSON.toJSONString(new MessageDto(userId, "list", data));
    }

    public static String receive(Long userId, Object data) {
        return JSON.toJSONString(new MessageDto(userId, "receive", data));
    }

}
