package com.Cang.controller;


import com.Cang.dto.Result;
import com.Cang.service.IShopTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author jdfcc
 * @since 2023-1-4
 */
@RestController
@RequestMapping("/shop-type")
public class ShopTypeController {
    @Resource
    private IShopTypeService typeService;

    @GetMapping("list")
    public Result queryTypeList() {
//        List<ShopType> typeList = typeService
//                .query().orderByAsc("sort").list();
//        return Result.ok(typeList);
        return typeService.selectList();
    }
}
