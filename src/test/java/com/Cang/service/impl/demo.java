package com.Cang.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description demo
 * @DateTime 2024/1/22 17:35
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class demo {

    @Test
    public void testConverter() {
        String string = new ArrayList<String>() {{
            add("foo");
            add("bar");
            add("too");
        }}.toString();
        System.out.println(string);
        ArrayList<String> list = CollectionUtil.toList(string);
        System.out.println(list);
    }
}
