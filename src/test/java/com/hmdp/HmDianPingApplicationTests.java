package com.hmdp;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class HmDianPingApplicationTests {


    @Test
    public void testCode(){
        String code= RandomUtil.randomNumbers(4);
        log.info("code: {}",code);
    }

}
