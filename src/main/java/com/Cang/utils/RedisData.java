package com.Cang.utils;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Jdfcc
 */
@Data
public class RedisData {
    private LocalDateTime expireTime;
    private Object data;
}
