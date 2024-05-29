package com.Cang.service.impl;


import com.Cang.entity.Game;
import com.Cang.entity.GameShow;
import com.Cang.mapper.GameShowMapper;
import com.Cang.service.GameShowService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description GameShowServiceImpl
 * @DateTime 2024/1/22 14:05
 */
@Service
public class GameShowServiceImpl extends ServiceImpl<GameShowMapper, GameShow> implements GameShowService {
    /**
     * 根据tag或者游戏名查找对应游戏
     *
     * @param index    当前页码
     * @param pageSize 查询页面大小
     * @param gameName 游戏名
     */
    @Override
    public Page<GameShow> query(Integer index, Integer pageSize,  String gameName) {
        // 创建查询对象
        LambdaQueryWrapper<GameShow> queryWrapper = new LambdaQueryWrapper<>();



        // 如果 name 参数不为空，则添加 like 条件
        if (!gameName.isEmpty()) {
            queryWrapper.like(GameShow::getName, gameName);
        }

        // 分页查询
        return page(new Page<>(index, pageSize), queryWrapper);
    }
}
