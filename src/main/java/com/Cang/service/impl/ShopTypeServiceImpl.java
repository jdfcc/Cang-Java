package com.Cang.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.Cang.dto.Result;
import com.Cang.entity.ShopType;
import com.Cang.mapper.ShopTypeMapper;
import com.Cang.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.Cang.constants.RedisConstants.CACHE_SHOP_TYPE_KEY;
import static com.Cang.constants.RedisConstants.CACHE_SHOP_TYPE_TTL;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jdfcc
 * @since 2022-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {
    private final ShopTypeMapper mapper;

    private final StringRedisTemplate stringRedisTemplate;

    public ShopTypeServiceImpl(ShopTypeMapper mapper, StringRedisTemplate stringRedisTemplate) {
        this.mapper = mapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

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

        if (shopTypeList == null) {
            return Result.fail("No store list yet");
        }
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shopTypeList));
        stringRedisTemplate.expire(key,CACHE_SHOP_TYPE_TTL, TimeUnit.MINUTES);
        return Result.ok(shopTypeList);
    }
}
