package com.Cang;

import cn.hutool.core.util.RandomUtil;
import com.Cang.dto.ChatDto;
import com.Cang.entity.Blog;
import com.Cang.entity.Chat;
import com.Cang.entity.Shop;
import com.Cang.mapper.BlogMapper;
import com.Cang.mapper.ChatMapper;
import com.Cang.mapper.FollowMapper;
import com.Cang.service.*;
import com.Cang.service.impl.MinIOFileStorageService;
import com.Cang.utils.HttpUtil;
import com.Cang.utils.IdGeneratorSnowflake;
import com.Cang.utils.UserHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static com.Cang.constants.RedisConstants.*;
import static com.Cang.constants.RedisConstants.CHAT_MESSAGE_USER_CACHE_KEY_LAST;
import static java.time.ZoneOffset.UTC;

@Slf4j
@RunWith(SpringRunner.class)
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
    void testSearch() {
        System.out.println(userService.getById(1010));
    }

    @Test
    public void testDel() {
        StringredisTemplate.opsForZSet().remove("jdfcc", "1232");
    }

    @Test
    public void testFollow() {
        List<String> follows = followMapper.getFollowerId(2L);
        log.info("follows: {}", follows);
    }

    @Test
    public void testChat() {
        Chat chat = new Chat();
        chat.setMessage("Hello");
        chat.setSend(1L);
        chat.setReceive(2L);
        chatService.sendMessage(chat);
    }

    @Test
    public void testHello() {
        Object o = redisTemplate.opsForHash().get("jdfcc", "name");
        if (o == null) {
            log.info("null");
        }
    }

    @Test
    public void testTime() {
        LocalDateTime dateTime = LocalDateTime.now();
        double seconds = dateTime.toEpochSecond(UTC);
        double milliseconds = seconds * 1000;
        System.out.println(milliseconds);

    }

    @Test
    public void testRandom(){
        String s = RandomUtil.randomString(15);
        System.out.println(s);
        UUID uuid = UUID.randomUUID();
        String fileName = uuid.toString() + ".jpg";
        System.out.println(fileName);
    }

    @Autowired
    MinIOFileStorageService minIOFileStorageService;
    @Test
    public void testMinUpload() throws FileNotFoundException {
        FileInputStream fileInputStream =
                new FileInputStream("C:\\Users\\Jdfcc\\Pictures\\memes.jpg");
        String s = minIOFileStorageService.uploadImgFile("kkk", "jjj", fileInputStream);
        System.out.println(s);
    }
    @Test
    public void testMinIo()  {
//        TODO 将minio抽象为miniostarter，学习设计模式中的策略模式？以根据不同文件类型实现上传不同文件

        try {
            FileInputStream fileInputStream =
                    new FileInputStream("C:\\Users\\Jdfcc\\Pictures\\memes.jpg");

            MinioClient client = MinioClient.builder().
                    credentials("jdfcc", "3039jdfcc").
                    endpoint("http://120.55.80.133:9000").build();
            PutObjectArgs putObjectArgs =PutObjectArgs.
                    builder().
                    object("test.jpg").
                    contentType("image/jpeg").
                    stream(fileInputStream,fileInputStream.available(),-1).
                    bucket("jdfcc").build();
            client.putObject(putObjectArgs);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testInteger(){
        Integer count=null;
        count=0;
        count+=1;
        System.out.println(count);
    }

    @Test
    public void testHash() {
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
    public void testIdGenerate() {
        long l = new IdGeneratorSnowflake().snowflakeId();
        log.info("@@@@@@@ {}", String.valueOf(l));
    }

    @Test
    public void testBlog() {
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

    public class Student {
        String name;
        String sex;

        Student() {
        }

        public Student init(String name, String sex) {
            Student student = new Student();
            student.name = name;
            student.sex = sex;
            return student;
        }
    }

    @Test
    public void testAnnotation() {
        Student stu = new Student();
        stu.init("李四", "男");
    }

    @Value("${my-log.src}")
    private String src;

    @Test
    public void testLog() throws FileNotFoundException {
        File file = new File((src));
        FileOutputStream outputStream = new FileOutputStream(file);

    }

    @Test
    public void testTimeOut()  {
        LocalTime currentTime = LocalTime.now();
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        Integer minute = currentTime.getMinute();
        Integer second = currentTime.getSecond();
        String time = hour + ":" + minute + ":" + second+"  ";
        log.info(time);
    }


    @Test
    public void testInsert() {
        String jdfcc = "111111111111obj";
        redisTemplate.opsForList().leftPush("1111111111", jdfcc);
        jdfcc = "2222222222222obj";
        redisTemplate.opsForList().leftPush("22222222222", jdfcc, 222);
    }

    @Test
    public void testNowTime(){
        LocalDateTime dateTime = LocalDateTime.now();
        Integer second = dateTime.getSecond();
        LocalDateTime localDateTime = dateTime.plusSeconds(10);
        long l = localDateTime.toEpochSecond(UTC);
        log.info("toEpochSecond {}", l);
        log.info("localDateTime {}", localDateTime);
        log.info("Now time {}",dateTime);
        log.info("dateTime {}",second);
    }

    @Test
    public void testRedis() {
        redisTemplate.opsForHash().put("j","time",LocalDateTime.now());
//        LocalDateTime o = (LocalDateTime) redisTemplate.opsForHash().get("j", "time");
//        System.out.println(o.toString());
    }

    @Test
    public void testChatSelect() {
        List<ChatDto> chat = chatMapper.selectLast(1010L);
//        List<String> keys = chatMapper.queryChatList(1010L);
        System.out.println(chat.toString());
//        System.out.println(keys);
    }

    @Test
    public void testChatsLast() {
        Long id = 1010L;
        Set<Object> chats = redisTemplate.opsForZSet().range(CHAT_MESSAGE_USER_CACHE_KEY_LAST + id, 0, -1);
        if (chats.isEmpty()) {
            log.info("重建缓存");
//            需要从数据库中重建缓存
            LambdaQueryWrapper<Chat> chatLambdaQueryWrapper = new LambdaQueryWrapper<>();

            chatLambdaQueryWrapper.eq(Chat::getSend, id);
            List<ChatDto> newChat = chatMapper.selectLast(UserHolder.getUser());
            for (Object tem : newChat) {
//                重建缓存
                long seconds = ((Chat) tem).getCreateTime().toEpochSecond(UTC);
                double score = seconds * 1000;
                redisTemplate.opsForZSet().add(CHAT_MESSAGE_USER_CACHE_KEY_LAST + id, tem, score);

            }
            System.out.println(newChat);
        }

    }


    @Test
    public void testAddL() {
        Long a = 2L;
        Long b = 3L;
        log.info("@@@@@@@@@@ {}", a + b);
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
    public void testRemove() {
        Boolean unliked = blogMapper.unliked(23L);
        log.info("flag: {}", unliked);
    }

    @Test
    public void test() {
        log.info("address: {}", address);
        log.info("password: {}", password);
    }





}
