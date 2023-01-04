package com.hmdp;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static com.hmdp.utils.RedisConstants.CACHE_SHOP_KEY;

@SpringBootTest
@Slf4j
class HmDianPingApplicationTests {


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
}
