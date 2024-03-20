package com.Cang.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description Tag
 * @DateTime 2024/2/22 19:02
 */
@Data
@TableName("tag")
public class Tag {
    private Long id;
    private String name;
    private String icon;
}
