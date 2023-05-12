package com.Cang.service;

import com.Cang.dto.Result;
import com.Cang.entity.Shop;
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

    Result updateShop(Shop shop);


}
