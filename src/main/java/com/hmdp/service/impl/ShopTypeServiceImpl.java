package com.hmdp.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.hmdp.utils.RedisConstants.CACHE_SHOP_TYPE_KEY;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {
    @Autowired
    private ShopTypeMapper mapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result selectList() {
        String key = CACHE_SHOP_TYPE_KEY;
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value != null) {
            List<ShopType> shopTypes = JSONUtil.toList(value, ShopType.class);
            return Result.ok(shopTypes);
        }

        LambdaQueryWrapper<ShopType> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(ShopType::getSort);
        List<ShopType> shopTypeList = mapper.selectList(wrapper);

        if (shopTypeList == null)
            return Result.fail("No store list yet");
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shopTypeList));
        return Result.ok(shopTypeList);
    }
}
