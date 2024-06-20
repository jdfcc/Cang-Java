package com.Cang.service;

import com.Cang.dto.GameDetailDto;
import com.Cang.entity.Game;
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

    /**
     * 根据tag或者游戏名查找对应游戏
     * @param index 当前页码
     * @param pageSize 查询页面大小
     * @param tagName tag名字
     * @param gameName 游戏名
     */
    Page<Game> query(Integer index, Integer pageSize, String tagName,String gameName);

    /**
     * 获取游戏详细信息
     * @param id 游戏id
     */
    GameDetailDto getGameDetail(String id);
}
