package com.hmdp;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static com.hmdp.utils.RedisConstants.CACHE_SHOP_KEY;

@SpringBootTest
@Slf4j
class HmDianPingApplicationTests {

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
    public void testAuth(){
        if(!auth)
            log.info("1");
    }

    @Test
    public void testNull(){
        String str="";
        if(str!=null)
            log.info("not null");
    }

}
