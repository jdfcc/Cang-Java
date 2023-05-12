package com.Cang;

import com.Cang.entity.Shop;
import com.Cang.mapper.BlogMapper;
import com.Cang.mapper.FollowMapper;
import com.Cang.service.IShopService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static com.Cang.utils.RedisConstants.SHOP_GEO_KEY;

@SpringBootTest
@Slf4j
class CangApplicationTests {
    @Value("${spring.redis.host}")
    private String address;

    @Value("${spring.redis.password}")
    private String password;

    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private FollowMapper followMapper;
    @Autowired
    private IShopService shopService;
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
    void loadShopData() {
        // 1.查询店铺信息
        List<Shop> list = shopService.list();
        // 2.把店铺分组，按照typeId分组，typeId一致的放到一个集合
        Map<Long, List<Shop>> map = list.stream().collect(Collectors.groupingBy(Shop::getTypeId));
        // 3.分批完成写入Redis
        for (Map.Entry<Long, List<Shop>> entry : map.entrySet()) {
            // 3.1.获取类型id
            Long typeId = entry.getKey();
            String key = SHOP_GEO_KEY + typeId;
            // 3.2.获取同类型的店铺的集合
            List<Shop> value = entry.getValue();
            List<RedisGeoCommands.GeoLocation<String>> locations = new ArrayList<>(value.size());
            // 3.3.写入redis GEOADD key 经度 纬度 member
            for (Shop shop : value) {
                // stringRedisTemplate.opsForGeo().add(key, new Point(shop.getX(), shop.getY()), shop.getId().toString());
                locations.add(new RedisGeoCommands.GeoLocation<>(
                        shop.getId().toString(),
                        new Point(shop.getX(), shop.getY())
                ));
            }
            redisTemplate.opsForGeo().add(key, locations);
        }
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


    @Test
    public void  testInt(){
        int value =1;
        Integer integer = new Integer(value);
        int s=integer;
        log.info("@@@@@@@@@@@@ {}",integer);
        log.info("------------------ {}",s);
        Set<String> strings = new HashSet<>();
        strings.add("foo");
        strings.add("bar");
        strings.add("1");
        System.out.println(strings.toString());
    }



}
