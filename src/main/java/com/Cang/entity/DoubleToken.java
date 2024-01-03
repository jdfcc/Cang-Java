package com.Cang.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description DoubleToken
 * @DateTime 2023/10/18 10:28
 */

@Data
@TableName("tb_token")
@ToString
public class DoubleToken {
    private Long id;
    private Long userId;
    /**
     * 刷新token，accessToken过期时靠此Token获取到新的accessToken
     */
    private String refreshToken;
    /**
     * 日常请求使用此token
     */
    private String accessToken;
}
