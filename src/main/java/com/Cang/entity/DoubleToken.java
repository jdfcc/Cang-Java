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
    private String refreshToken;
    private String accessToken;
}
