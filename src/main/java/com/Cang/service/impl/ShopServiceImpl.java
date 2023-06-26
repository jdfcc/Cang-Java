package com.Cang.service.impl;

import com.Cang.dto.Result;
import com.Cang.entity.Shop;
import com.Cang.mapper.ShopMapper;
import com.Cang.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.Cang.utils.CacheClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.TimeUnit;

import static com.Cang.constants.RedisConstants.*;

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

    private final StringRedisTemplate redisTemplate;

    private final ShopMapper shopMapper;
    private final CacheClient cacheClient;

    public ShopServiceImpl(StringRedisTemplate redisTemplate, ShopMapper shopMapper, CacheClient cacheClient) {
        this.redisTemplate = redisTemplate;
        this.shopMapper = shopMapper;
        this.cacheClient = cacheClient;
    }

    @Override
    public Result selectShopById(Long id) {
//        String key = CACHE_SHOP_KEY + id;
//        String value = redisTemplate.opsForValue().get(key);
//
////        value不为空，直接返回
//        if (StrUtil.isNotBlank(value)) {
//            Shop shop = JSONUtil.toBean(value, Shop.class);
//            log.info(String.valueOf(shop));
//            return Result.ok(shop);
//        }
////        命中的为空值
//        if (value != null) {
//            return Result.fail("The store does not exist.");
//        }
////如果shop为空，在数据库里面查
//        Shop shop = null;
//        try {
//            Boolean lock = getLock(LOCK_KEY);
////        获取失败
//            if (!lock) {
//                Thread.sleep(THREAD_SLEEP_TIME);
//                selectShopById(id);
//            }
//
//            LambdaQueryWrapper<Shop> wrapper = new LambdaQueryWrapper<>();
//            wrapper.eq(Shop::getId, id);
//            shop = shopMapper.selectOne(wrapper);
//
//            if (shop == null) {
//                //            redis写空值解决缓存穿透
//                redisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
//                return Result.fail("The store does not exist.");
//            }
////        doubleCheck,因为线程在休眠过程中其他线程可能已经重建好了redis数据
//            value = redisTemplate.opsForValue().get(key);
//            if (StrUtil.isNotBlank(value)) {
//                shop = JSONUtil.toBean(value, Shop.class);
//                log.info(String.valueOf(shop));
//                return Result.ok(shop);
//            }
//
////        redis写入真实值
//            redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop));
//            redisTemplate.expire(key, CACHE_SHOP_TTL, TimeUnit.MINUTES);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        } finally {
//            removeLock(LOCK_KEY);
//        }
//        return Result.ok(shop);
        Shop shop = cacheClient.queryWithLogicalExpire(
                CACHE_SHOP_KEY, id, Shop.class, this::getById, CACHE_SHOP_TTL, TimeUnit.SECONDS);
        if (shop == null) {
            shop = cacheClient.queryWithCacheThrough(CACHE_SHOP_KEY,id,Shop.class,this::getById,CACHE_SHOP_TTL,TimeUnit.MINUTES);
            cacheClient.setWithLogicExpire(CACHE_SHOP_KEY + id, shop, CACHE_SHOP_TTL, TimeUnit.SECONDS);
        }
        return Result.ok(shop);
    }


    @Override
    @Transactional
    public Result updateShop(Shop shop) {
        if (shop.getId() == null) {
            return Result.fail("ID could not be empty");
        }
        shopMapper.updateById(shop);
        redisTemplate.delete(CACHE_SHOP_KEY + shop.getId());
        return Result.ok();
    }

//    /**
//     * 获取互斥锁，如果获取成功返回true
//     *
//     * @param key
//     * @return
//     */
//    public Boolean getLock(String key) {
//        Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, LOCK_VALUE, LOCK_TTL, TimeUnit.SECONDS);
//        return BooleanUtil.isTrue(flag);
//    }
//
//    /**
//     * 移除互斥锁
//     *
//     * @param key
//     */
//    public void removeLock(String key) {
//        redisTemplate.delete(key);
//    }

}
