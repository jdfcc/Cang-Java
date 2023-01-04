package com.hmdp.service;

import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jdfcc
 * @since 2023-1-4
 *
 */
public interface IShopService extends IService<Shop> {

    Result selectShopById(Long id);


}
