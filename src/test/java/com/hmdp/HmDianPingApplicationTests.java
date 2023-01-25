package com.hmdp;

import cn.hutool.core.util.RandomUtil;
import com.hmdp.service.IShopService;
import com.hmdp.service.impl.ShopServiceImpl;
import com.hmdp.utils.CacheClient;
import com.hmdp.utils.RedisIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.hmdp.utils.RedisConstants.CACHE_SHOP_KEY;

@SpringBootTest
@Slf4j
class HmDianPingApplicationTests {
    @Value("${spring.redis.host}")
    private String address;

    @Value("${spring.redis.password}")
    private String password;

    @Test
    public void test(){
        log.info("address: {}",address);
        log.info("password: {}",password);
    }


//
//    @Autowired
//    private ShopServiceImpl shopServiceImpl;
//
//    @Autowired
//    private CacheClient cacheClient;
//
//    @Autowired
//    private IShopService service;
//    @Autowired
//    private RedisIdWorker redisIdWorker;
//
//    private ExecutorService es = Executors.newFixedThreadPool(500);


//    @org.junit.jupiter.api.Test
//    public void testIdWorker() throws InterruptedException {
//        CountDownLatch latch = new CountDownLatch(300);
//
//        Runnable task = () -> {
//            for (int i = 0; i < 100; i++) {
//                long id = redisIdWorker.nextId("order");
//                System.out.println("id = " + id);
//            }
//            latch.countDown();
//        };
//        long begin = System.currentTimeMillis();
//        for (int i = 0; i < 300; i++) {
//            es.submit(task);
//        }
//        latch.await();
//        long end = System.currentTimeMillis();
//        System.out.println("time = " + (end - begin));
//    }
//
////    @Test
////    public void testCache() {
////        Long id=1L;
////        Shop shop = cacheClient.queryWithCacheThrough(
////                CACHE_SHOP_KEY, id, Shop.class,this::select,CACHE_SHOP_TTL, TimeUnit.MINUTES);
////        cacheClient.setWithLogicExpire(CACHE_SHOP_KEY, );
////    }
//
//    @Value("${login.authorization}")
//    private Boolean auth;
//
//    @org.junit.jupiter.api.Test
//    public void testCode() {
//        String code = RandomUtil.randomNumbers(4);
//        log.info("code: {}", code);
//    }
//
//    @org.junit.jupiter.api.Test
//    public void testName() {
//        Long id = 0x8000000000000000L;
////        cache:shop:-9223372036854775808
//        String key = CACHE_SHOP_KEY + id;
//        log.info(key);
//    }
//
//    @org.junit.jupiter.api.Test
//    public void testAuth() {
//        if (!auth)
//            log.info("1");
//    }
//
//    @org.junit.jupiter.api.Test
//    public void testNull() {
//        String str = "";
//        if (str != null)
//            log.info("not null");
//    }

}
