package com.Cang;


import com.Cang.entity.Game;
import com.Cang.service.GameService;
import com.Cang.service.impl.MinIOFileStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.Data;
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
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description JsonParse
 * @DateTime 2024/1/4 16:29
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JsonParse {

    @Autowired
    public GameService gameService;

    @Test
    public void parser() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Game> gameList = new ArrayList<>();

            // 从文件中读取 JSON 数据
            String filePath = "C:\\Users\\Jdfcc\\Desktop\\steam-scraper\\output\\products_all copy.jl"; // 请替换为你的文件路径
            String jsonFileContent = readFromFile(filePath);

            String[] jsonObjects = jsonFileContent.split("\n");

            for (String jsonObject : jsonObjects) {
                Game game = convertJsonToGame(objectMapper.readValue(jsonObject, Map.class));
                gameList.add(game);
            }
            // 打印结果
            for (Game game : gameList) {
                System.out.println(game);
            }
            gameService.saveBatch(gameList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readFromFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private Game convertJsonToGame(Map<String, Object> jsonMap) {
        Game game = new Game();
        System.out.println(jsonMap);
        game.setUrl((String) jsonMap.get("url"));
        game.setReviewsUrl((String) jsonMap.get("reviews_url"));
        game.setId(Long.valueOf((String) jsonMap.get("id")));
        game.setTitle((String) jsonMap.get("title"));
        List<String> genres = (List<String>) jsonMap.get("genres");
        if (genres != null) {
//            game.setGenres(genres);
        }
        game.setDeveloper((String) jsonMap.get("developer"));
        game.setPublisher((String) jsonMap.get("publisher"));
        String releaseDate = (String) jsonMap.get("release_date");
        game.setReleaseDate(releaseDate);
        game.setAppName((String) jsonMap.get("app_name"));
        List<String> tags = (List<String>) jsonMap.get("tags");
        if (tags != null) {
//            game.setTags(tags);
        }
        String discount = (String) jsonMap.get("discount_price");
        if (discount != null) {
            game.setDiscountPrice(discount);

        }

//        game.setPrice(Integer.valueOf((String) jsonMap.get("price")));

        String obj = (String) jsonMap.get("price");
        if (obj != null) {
            if (obj.contains("Free")) {
                game.setPrice(0F);
            } else {
                String[] s = obj.split(" ");
                game.setUnit(s[0]);
                game.setPrice(Float.valueOf(s[1]));
            }

        }
        game.setSentiment((String) jsonMap.get("sentiment"));
        game.setEarlyAccess((Boolean) jsonMap.get("early_access"));
        return game;
    }

//    @Data
//    public class Game implements Serializable {
//        private String url;
//        private String reviews_url;
//        private String id;
//        private String title;
//        private List<String> genres;
//        private String developer;
//        private String publisher;
//        private String release_date;
//        private String app_name;
//        private List<Object> tags;
//        private String discount_price;
//        private String price;
//        private String sentiment;
//        private boolean early_access;
//    }
}
