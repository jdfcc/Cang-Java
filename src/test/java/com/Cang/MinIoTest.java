package com.Cang;

import com.Cang.service.impl.MinIOFileStorageService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;


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
        for (int i = 0; i < 500; i++) {
            FileInputStream fileInputStream =
                    new FileInputStream("C:\\Users\\Jdfcc\\Pictures\\memes.jpg");
            String s = minIoFileStorageService.uploadImgFile("kkk", "jjj"+String.valueOf(i), fileInputStream);
            System.out.println(s);
        }
    }

    @Test
    public void delectable() {
        minIoFileStorageService.delete("http://120.55.80.133:9000/jdfcc/kkk/2024/01/05/jjj");
    }

}
