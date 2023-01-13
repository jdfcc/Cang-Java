package com.hmdp.entity;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class RedisData {
    private Object date;
    private LocalDateTime expireTime;

}
