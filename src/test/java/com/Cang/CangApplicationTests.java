package com.Cang;

import com.Cang.entity.Blog;
import com.Cang.entity.Chat;
import com.Cang.entity.Shop;
import com.Cang.mapper.BlogMapper;
import com.Cang.mapper.FollowMapper;
import com.Cang.service.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static com.Cang.utils.RedisConstants.CHAT_MESSAGE_KEY;
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
    private IUserInfoService userService;

    @Autowired
    private IBlogService blogService;
    @Autowired
    private FollowMapper followMapper;
    @Autowired
    private IShopService shopService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private StringRedisTemplate StringredisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void testSearch(){
        System.out.println(userService.getById(1010));
    }
    @Test
    public void testDel(){
        StringredisTemplate.opsForZSet().remove("jdfcc","1232");
    }

    @Test
    public void testFollow(){
        List<String> follows = followMapper.getFollowerId(2L);
        log.info("follows: {}",follows);
    }

    @Test
    public void testChat(){
        Chat chat = new Chat();
        chat.setMessage("Hello");
        chat.setSend(1L);
        chat.setReceive(2L);
        chatService.sendMessage(chat);
    }

    @Test
    public void testTime(){
        LocalDateTime dateTime = LocalDateTime.now();
        double seconds = dateTime.toEpochSecond(ZoneOffset.UTC);
        double milliseconds = seconds * 1000;
        System.out.println(milliseconds);
    }
    @Test
    public void testHash(){
//        Chat chat = new Chat();
//        chat.setMessage("Hello");
//        chat.setSend(1L);
//        chat.setReceive(2L);
//        redisTemplate.opsForHash().put(CHAT_MESSAGE_KEY,String.valueOf(1013L),chat);
////        StringredisTemplate.opsForHash().put(CHAT_MESSAGE_KEY,String.valueOf(1014L),chat);
//        redisTemplate.opsForHash().put(CHAT_MESSAGE_KEY,String.valueOf(1011L),chat);
//        Chat o = (Chat) redisTemplate.opsForHash().get(CHAT_MESSAGE_KEY, String.valueOf(1013L));
//        System.out.println(o.getMessage());
    }

    @Test
    public void testBlog(){
        Blog blog = new Blog();
        blog.setId(222L);
        blog.setCreateTime(LocalDateTime.now());
        blog.setShopId(10L);
        blog.setUserId(2L);
        blog.setTitle("Blog");
        blog.setContent("123231");
        blog.setImages("1111");
        blogService.save(blog);

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
            StringredisTemplate.opsForGeo().add(key, locations);
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
