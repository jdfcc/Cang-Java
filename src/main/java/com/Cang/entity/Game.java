package com.Cang.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description TODO
 * @DateTime 2024/1/4 16:23
 * <p>
 * Copyright 2024 json.cn
 */

@Data
public class Game implements Serializable {

    private String url;
    private String reviews_url;
    private String id;
    private String title;
    private List<String> genres;
    private String developer;
    private String publisher;
    private String release_date;
    private String app_name;
    private List<String> tags;
    private String discount_price;
    private String price;
    private String sentiment;
    private boolean early_access;

}