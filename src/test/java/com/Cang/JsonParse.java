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
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description JsonParse
 * @DateTime 2024/1/4 16:29
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JsonParse {

    @Test
    public void parser() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Game> gameList = new ArrayList<>();

            // 从文件中读取 JSON 数据
            String filePath = "C:\\Users\\Jdfcc\\Desktop\\steam-scraper\\output\\products_all.jl"; // 请替换为你的文件路径
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

    private static Game convertJsonToGame(Map<String, Object> jsonMap) {
        Game game = new Game();
        game.setUrl((String) jsonMap.get("url"));
        game.setReviews_url((String) jsonMap.get("reviews_url"));
        game.setId((String) jsonMap.get("id"));
        game.setTitle((String) jsonMap.get("title"));
        game.setGenres((List<String>) jsonMap.get("genres"));
        game.setDeveloper((String) jsonMap.get("developer"));
        game.setPublisher((String) jsonMap.get("publisher"));
        String releaseDate = (String) jsonMap.get("release_date");
        game.setRelease_date(releaseDate);

        game.setApp_name((String) jsonMap.get("app_name"));
        game.setTags((List<String>) jsonMap.get("tags"));
        game.setDiscount_price((String) jsonMap.get("discount_price"));
        game.setPrice((String) jsonMap.get("price"));
        game.setSentiment((String) jsonMap.get("sentiment"));
        game.setEarly_access((Boolean) jsonMap.get("early_access"));

        return game;
    }
}
