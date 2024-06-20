package com.Cang.service;

import com.Cang.entity.Game;
import com.Cang.entity.GameShow;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description GameShowService
 * @DateTime 2024/1/22 14:04
 */
public interface GameShowService extends IService<GameShow> {
    /**
     * 根据tag或者游戏名查找对应游戏
     * @param index 当前页码
     * @param pageSize 查询页面大小
     * @param gameName 游戏名
     */
    Page<GameShow> query(Integer index, Integer pageSize, String gameName);
}
