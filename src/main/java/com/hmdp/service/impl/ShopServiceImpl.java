package com.hmdp.service.impl;


import cn.hutool.core.util.StrUtil;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.*;

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
        if (StrUtil.isNotBlank(value)) {
            Shop shop = JSONUtil.toBean(value, Shop.class);
            log.info(String.valueOf(shop));
            return Result.ok(shop);
        }
//        命中的为空值
        if (value != null) {
            return Result.fail("The store does not exist.");
        }
//如果shop为空，在数据库里面查
        LambdaQueryWrapper<Shop> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Shop::getId, id);
        Shop shop = shopMapper.selectOne(wrapper);
        if (shop == null) {
            redisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            return Result.fail("The store does not exist.");
        }
//        写入redis
        redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop));
        redisTemplate.expire(key, CACHE_SHOP_TTL, TimeUnit.MINUTES);

        return Result.ok(shop);
    }

    @Override
    @Transactional
    public Result updateShop(Shop shop) {
        if (shop.getId() == null)
            return Result.fail("ID could not be empty");
        shopMapper.updateById(shop);
        redisTemplate.delete(CACHE_SHOP_KEY + shop.getId());
        return Result.ok();
    }
}
