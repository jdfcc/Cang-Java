package com.hmdp.service.impl;


import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.hmdp.utils.RedisConstants.CACHE_SHOP_KEY;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jdfcc
 * @since 2023-1-4
 */
@Service
@Slf4j
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private IShopService service;

    @Override
    public Result selectShopById(Long id) {

        String key = CACHE_SHOP_KEY + id;
        String value = redisTemplate.opsForValue().get(key);
//        value不为空，直接返回
        if (value != null) {
            Shop shop = JSONUtil.toBean(value, Shop.class);
            log.info(String.valueOf(shop));
            return Result.ok(shop);
        }
//如果shop为空，在数据库里面查
        LambdaQueryWrapper<Shop> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Shop::getId, id);
        Shop shop = shopMapper.selectOne(wrapper);
        if (shop == null) return Result.fail("The store does not exist.");
//        写入redis
        redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop));

        return Result.ok(shop);
    }


}
