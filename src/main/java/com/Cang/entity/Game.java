package com.Cang.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description Game
 * @DateTime 2024/1/4 16:23
 * <p>
 * Copyright 2024 json.cn
 */

@Data
@TableName("game")
public class Game implements Serializable {
    private String url;
    private String reviewsUrl;
    private Long id;
    private String title;
    private String genres;
    private String developer;
    private String publisher;
    private String releaseDate;
    private String appName;
    private String tags;
    private String discountPrice;
    private Float price;
    private String unit;
    private String sentiment;
    private boolean earlyAccess;

    public void setGenres(List<String> genre) {
        this.genres = genre.toString();
    }

    public void setTags(List<String> tag) {
        this.tags = tag.toString();
    }

}
