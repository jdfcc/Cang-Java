package com.Cang.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/**
 * @author Jdfcc
 */
@Data
@Component
public class RedisData {
    private Object data;
    private LocalDateTime expireTime;

}
