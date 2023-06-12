package com.Cang.service;

import com.Cang.dto.Result;
import com.Cang.entity.Follow;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *  服务类
 *
 * @author jdfcc
 * @since 2022-12-22
 */
public interface IFollowService extends IService<Follow> {

    Result follow(Long id, Boolean isFollow);

    Result isFollow(Long id);

    Result common(Long id);
}
