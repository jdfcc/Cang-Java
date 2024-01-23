package com.Cang.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description GamePic
 * @DateTime 2024/1/5 9:25
 */
@Data
@TableName("game_show")
public class GameShow {
    private Long id;
    private String name;
    private String url;
    private String steamPic;
    private String myPic;
}
