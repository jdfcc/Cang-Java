package com.Cang.service;

import com.Cang.entity.Game;
import com.Cang.entity.Tag;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description TagService
 * @DateTime 2024/2/22 19:04
 */
public interface TagService extends IService<Tag> {
    /**
     * 获取游戏tag
     *
     * @param index    当前地址
     * @param pageSize 获取数量
     * @return list
     */
    Page<Tag> getTags(Integer index, Integer pageSize);

}
