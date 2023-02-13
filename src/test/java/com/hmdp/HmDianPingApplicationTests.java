package com.hmdp;

import cn.hutool.core.util.RandomUtil;
import com.hmdp.entity.Blog;
import com.hmdp.mapper.BlogMapper;
import com.hmdp.mapper.FollowMapper;
import com.hmdp.service.IShopService;
import com.hmdp.service.impl.ShopServiceImpl;
import com.hmdp.utils.CacheClient;
import com.hmdp.utils.RedisIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.List;
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

    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private FollowMapper followMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Test
    public void testDel(){
        redisTemplate.opsForZSet().remove("jdfcc","1232");
    }

    @Test
    public void testFollow(){
        List<String> follows = followMapper.getFollowerId(2L);
        log.info("follows: {}",follows);
    }

    @Test
    public void testRemove(){
        Boolean unliked = blogMapper.unliked(23L);
        log.info("flag: {}",unliked);
    }

    @Test
    public void test(){
        log.info("address: {}",address);
        log.info("password: {}",password);
    }




}
