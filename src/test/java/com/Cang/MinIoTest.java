package com.Cang;

import com.Cang.entity.Game;
import com.Cang.service.impl.MinIOFileStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Jdfcc
 * @DateTime 2023/7/17 22:58
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MinIoTest {

    @Autowired
    public MinIOFileStorageService minIoFileStorageService;


    @Test
    public void testMinIo() throws Exception {
        try {
            FileInputStream fileInputStream =
                    new FileInputStream("C:\\Users\\Jdfcc\\Pictures\\memes.jpg");

            MinioClient client = MinioClient.builder().
                    credentials("jdfcc", "3039jdfcc").
                    endpoint("http://120.55.80.133:9000").build();
            PutObjectArgs putObjectArgs = PutObjectArgs.
                    builder().
                    object("test.jpg").
                    contentType("image/jpeg").
                    stream(fileInputStream, fileInputStream.available(), -1).
                    bucket("jdfcc").build();
            client.putObject(putObjectArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void contextLoads() throws FileNotFoundException {
        FileInputStream fileInputStream =
                new FileInputStream("C:\\Users\\Jdfcc\\Pictures\\memes.jpg");
        String s = minIoFileStorageService.uploadImgFile("kkk", "jjj", fileInputStream);
        System.out.println(s);
    }

    @Test
    public void delectable() {
        minIoFileStorageService.delete("http://120.55.80.133:9000/jdfcc/kkk/2024/01/05/jjj");
    }

}
