package com.Cang.service.impl;

import com.Cang.dto.GameDetailDto;
import com.Cang.dto.Result;
import com.Cang.entity.Game;
import com.Cang.entity.Tag;
import com.Cang.mapper.GameMapper;
import com.Cang.service.GameService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description GameServiceImpl
 * @DateTime 2024/1/10 16:59
 */
@Service
public class GameServiceImpl extends ServiceImpl<GameMapper, Game> implements GameService {

    @Resource
    private GameMapper gameMapper;

    /**
     * 根据数据库中获取游戏tag，数据来源为GameService，需要做全盘扫描
     *
     * @param index    当前坐标
     * @param pageSize 默认查询尺寸
     * @return tags of the game
     */
    @Override
    public List<String> getTags(Integer index, Integer pageSize) {
        List<String> tags = gameMapper.getTags();
        HashSet<String> strings = new HashSet<>(tags);
        System.out.println(strings.size());
        return null;
    }

    /**
     * @param index    坐标
     * @param pageSize 分页尺寸
     * @param tagName  tagName
     * @return Page<Game>
     */
    @Override
    public List<Game> getGameByTag(Integer index, int pageSize, String tagName) {
        return gameMapper.getByTag(tagName, index, pageSize);
    }

    /**
     * 根据tag或者游戏名查找对应游戏
     *
     * @param index    当前页码
     * @param pageSize 查询页面大小
     * @param tagName  tag名字
     * @param gameName 游戏名
     */
    @Override
    public Page<Game> query(Integer index, Integer pageSize, String tagName, String gameName) {
        // 创建查询对象
        LambdaQueryWrapper<Game> queryWrapper = new LambdaQueryWrapper<>();

        // 如果 type 参数不为空，则添加 like 条件
        if (!tagName.isEmpty()) {
            queryWrapper.like(Game::getGenres, tagName);
        }

        // 如果 name 参数不为空，则添加 like 条件
        if (!gameName.isEmpty()) {
            queryWrapper.like(Game::getAppName, gameName);
        }

        // 分页查询
        return page(new Page<>(index, pageSize), queryWrapper);
    }

    /**
     * 获取游戏详细信息
     *
     * @param id 游戏id
     */
    @Override
    public GameDetailDto getGameDetail(String id) {
        return gameMapper.getDetail(id);
    }
}
