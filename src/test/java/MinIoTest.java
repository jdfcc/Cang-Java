//import io.minio.MinioClient;
//import io.minio.PutObjectArgs;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//
///**
// * @author Jdfcc
// * @Description TODO
// * @DateTime 2023/7/17 22:58
// */
//@RunWith(SpringRunner.class)
//public class MinIoTest {
//
//    @Test
//    public void testMinIo() throws Exception {
//        try {
//            FileInputStream fileInputStream =
//                    new FileInputStream("C:\\Users\\Jdfcc\\Pictures\\memes.jpg");
//
//            MinioClient client = MinioClient.builder().
//                    credentials("jdfcc", "3039jdfcc").
//                    endpoint("http://120.55.80.133:9000").build();
//            PutObjectArgs putObjectArgs =PutObjectArgs.
//                    builder().
//                    object("test.jpg").
//                    contentType("image/jpeg").
//                    stream(fileInputStream,fileInputStream.available(),-1).
//                    bucket("jdfcc").build();
//            client.putObject(putObjectArgs);
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//
//    }
//}
