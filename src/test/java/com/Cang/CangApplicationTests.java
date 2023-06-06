package com.Cang;

import com.Cang.dto.ChatDto;
import com.Cang.dto.Result;
import com.Cang.entity.Blog;
import com.Cang.entity.Chat;
import com.Cang.entity.Shop;
import com.Cang.mapper.BlogMapper;
import com.Cang.mapper.ChatMapper;
import com.Cang.mapper.FollowMapper;
import com.Cang.service.*;
import com.Cang.utils.IdGeneratorSnowflake;
import com.Cang.utils.UserHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static com.Cang.utils.RedisConstants.*;
import static com.Cang.utils.RedisConstants.CHAT_MESSAGE_USER_CACHE_KEY_LAST;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

    @Autowired
    private ChatMapper chatMapper;
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
    public void testHello(){
        Object o = redisTemplate.opsForHash().get("jdfcc", "name");
        if(o==null){
            log.info("null");
        }
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
    public void testIdGenerate(){
        long l = new IdGeneratorSnowflake().snowflakeId();
        log.info("@@@@@@@ {}",String.valueOf(l));
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
    public void testInsert(){
        String jdfcc = "111111111111obj";
        redisTemplate.opsForList().leftPush("1111111111",jdfcc);
        jdfcc="2222222222222obj";
        redisTemplate.opsForList().leftPush("22222222222",jdfcc,222);
    }


    @Test
    public void testChatSelect(){
        List<ChatDto> chat = chatMapper.selectLast(1010L);
//        List<String> keys = chatMapper.queryChatList(1010L);
        System.out.println(chat.toString());
//        System.out.println(keys);
    }

    @Test
    public void testChatsLast(){
        Long id = 1010L;
        Set<Object> chats = redisTemplate.opsForZSet().range(CHAT_MESSAGE_USER_CACHE_KEY_LAST + id, 0, -1);
        if (chats .isEmpty()) {
            log.info("重建缓存");
//            需要从数据库中重建缓存
            LambdaQueryWrapper<Chat> chatLambdaQueryWrapper = new LambdaQueryWrapper<>();

            chatLambdaQueryWrapper.eq(Chat::getSend, id);
            List<ChatDto> newChat = chatMapper.selectLast(UserHolder.getUser().getId());
            for (Object tem : newChat) {
//                重建缓存
                long seconds = ((Chat) tem).getCreateTime().toEpochSecond(ZoneOffset.UTC);
                double score = seconds * 1000;
                redisTemplate.opsForZSet().add(CHAT_MESSAGE_USER_CACHE_KEY_LAST + id, tem, score);

            }
            System.out.println(newChat);
        }

    }

    @Test
    public void testAddL(){
        Long a=2L;
        Long b=3L;
        log.info("@@@@@@@@@@ {}",a+b);
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
