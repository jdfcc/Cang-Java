package com.hmdp;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import com.hmdp.service.impl.ShopServiceImpl;
import com.hmdp.utils.CacheClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.CACHE_SHOP_KEY;
import static com.hmdp.utils.RedisConstants.CACHE_SHOP_TTL;

@SpringBootTest
@Slf4j
class HmDianPingApplicationTests {

    @Autowired
    private ShopServiceImpl shopServiceImpl;

    @Autowired
    private CacheClient cacheClient;

    @Autowired
    private IShopService service;

//    @Test
//    public void testCache() {
//        Long id=1L;
//        Shop shop = cacheClient.queryWithCacheThrough(
//                CACHE_SHOP_KEY, id, Shop.class,this::select,CACHE_SHOP_TTL, TimeUnit.MINUTES);
//        cacheClient.setWithLogicExpire(CACHE_SHOP_KEY, );
//    }

    @Value("${login.authorization}")
    private Boolean auth;

    @Test
    public void testCode() {
        String code = RandomUtil.randomNumbers(4);
        log.info("code: {}", code);
    }

    @Test
    public void testName() {
        Long id = 0x8000000000000000L;
//        cache:shop:-9223372036854775808
        String key = CACHE_SHOP_KEY + id;
        log.info(key);
    }

    @Test
    public void testAuth() {
        if (!auth)
            log.info("1");
    }

    @Test
    public void testNull() {
        String str = "";
        if (str != null)
            log.info("not null");
    }

}
