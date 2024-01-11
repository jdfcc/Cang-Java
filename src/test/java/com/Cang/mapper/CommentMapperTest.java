package com.Cang.mapper;

import com.Cang.entity.Comment;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.List;


/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description CommentMapperTest
 * @DateTime 2024/1/11 9:40
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommentMapperTest {

    @Resource
    private CommentMapper commentMapper;

    @Test
    void page() {
        List<Comment> page = commentMapper.page(9349396L, 1, 2);
        log.info(page.toString());
    }
}