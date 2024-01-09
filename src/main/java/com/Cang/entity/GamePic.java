package com.Cang.entity;

import lombok.Data;

import java.util.List;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description GamePic
 * @DateTime 2024/1/5 9:25
 */
@Data
public class GamePic {
    private String url;
    private List<String> detailUrls;
}
