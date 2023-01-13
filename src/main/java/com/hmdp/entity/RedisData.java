package com.hmdp.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Data
@Component
public class RedisData {
    private Object data;
    private LocalDateTime expireTime;

}
