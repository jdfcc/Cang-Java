package com.Cang.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Jdfcc
 * @Description ChatDto
 * @DateTime 2023/6/4 18:44
 */
@Data
@NoArgsConstructor
public class ChatDto implements Serializable {

    /**
     * 消息id
     */
    @JsonSerialize(using = ToStringSerializer.class)
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
    @JsonSerialize(using = ToStringSerializer.class)
    private Long send;
    /**
     * 接收者
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long receive;

    /**
     * 创建时间
     */
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 昵称
     */
    private String nickName;

}
