package com.Cang.service.impl;

import com.Cang.dto.CommentResultDto;
import com.Cang.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description TODO
 * @DateTime 2024/1/11 9:58
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommentServiceImplTest {

    @Resource
    private CommentService commentService;
    @Test
    void listComments() {
        List<CommentResultDto> commentResultDtos = commentService.listComments(9349396L, 1, 20);
        System.out.println(commentResultDtos.toString());
    }
}