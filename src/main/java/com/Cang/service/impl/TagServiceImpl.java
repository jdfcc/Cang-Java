package com.Cang.service.impl;

import com.Cang.dto.Result;
import com.Cang.entity.Game;
import com.Cang.entity.Tag;
import com.Cang.mapper.TagMapper;
import com.Cang.service.GameService;
import com.Cang.service.TagService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description TagServiceImpl
 * @DateTime 2024/2/22 19:05
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    @Resource
    private GameService gameService;

    /**
     * 获取游戏tag
     *
     * @param index    当前地址
     * @param pageSize 获取数量
     * @return list
     */
    @Override
    public Page<Tag> getTags(Integer index, Integer pageSize) {
        return this.query()
                .page(new Page<>(index, pageSize));
    }


}
