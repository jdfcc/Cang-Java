package com.Cang.service;

import com.Cang.entity.Game;
import com.Cang.entity.Tag;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description GameService
 * @DateTime 2024/1/10 16:59
 */
public interface GameService extends IService<Game> {
    /**
     * 获取游戏tag
     * @return tags of the game
     */
    List<String> getTags(Integer index,Integer pageSize);

    List<Game> getGameByTag(Integer index, int pageSize, String tagName);
}
