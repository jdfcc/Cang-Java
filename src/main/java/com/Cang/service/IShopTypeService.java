package com.Cang.service;

import com.Cang.dto.Result;
import com.Cang.entity.ShopType;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jdfcc
 * @since 2022-12-22
 */
public interface IShopTypeService extends IService<ShopType> {

    Result selectList();
}
