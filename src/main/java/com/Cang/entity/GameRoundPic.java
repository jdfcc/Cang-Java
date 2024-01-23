package com.Cang.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description GameRoundPic
 * @DateTime 2024/1/22 17:22
 */
@Data
public class GameRoundPic implements Serializable {
    private Long id;
    private String name;
    private String url;
    /**
     * steam原始图片链接
     */
    private String steamImages;
    /**
     * 本地上传的图片链接
     */
    private String myImages;

    public void setSteamImages(List<String> steamImages) {
        this.steamImages = steamImages.toString();
    }

    public void setMyImages(List<String> myImages) {
        this.myImages = myImages.toString();
    }
}
