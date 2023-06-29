package com.Cang.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Jdfcc
 * @Description CheckIp Entity
 * @DateTime 2023/6/28 18:31
 */
@Data
public class CheckIp {

    /**
     * IP地址
     */
    private String ip;

    /**
     * 访问次数
     */
    private Integer count;

    /**
     * 访问时间
     */
    private LocalDateTime accessTime;

}
