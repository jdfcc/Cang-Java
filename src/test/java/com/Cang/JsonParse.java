package com.Cang;


import com.Cang.entity.Game;
import com.Cang.entity.GameRoundPic;
import com.Cang.entity.GameShow;
import com.Cang.service.GameRoundPicService;
import com.Cang.service.GameService;
import com.Cang.service.GameShowService;
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

import javax.annotation.Resource;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
    @Resource
    public GameShowService gameShowService;

    @Resource
    GameRoundPicService gameRoundPicService;

    @Test
    public void parser() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Game> gameList = new ArrayList<>();

            // 从文件中读取 JSON 数据
            String filePath = "E:\\steam-scraper\\output\\products_all copy.jl"; // 请替换为你的文件路径
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
            List<Game> distinctList = new ArrayList<>(
                    gameList.stream().collect(Collectors.toMap(Game::getId, obj -> obj, (existing, replacement) -> existing)).values()
            );
            gameService.saveBatch(distinctList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 展示页面游戏获取
     */
    @Test
    public void mainGamePicParser() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<GameShow> gameList = new ArrayList<>();

            // 从文件中读取 JSON 数据
            String filePath = "E:\\steam-scraper\\output.json"; // 请替换为你的文件路径
            String jsonFileContent = readFromFile(filePath);

            String[] jsonObjects = jsonFileContent.split("\n");

            for (String jsonObject : jsonObjects) {
                GameShow game = convertJsonToGameShow(objectMapper.readValue(jsonObject, Map.class));
                gameList.add(game);
            }
            // 打印结果
            for (GameShow game : gameList) {
                System.out.println(game);
            }
            List<GameShow> distinctList = new ArrayList<>(
                    gameList.stream().collect(Collectors.toMap(GameShow::getId, obj -> obj, (existing, replacement) -> existing)).values()
            );
            gameShowService.saveBatch(distinctList);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 详情页游戏轮播图获取
     */
    @Test
    public void detailGamePicParser() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<GameRoundPic> gameList = new ArrayList<>();

            // 从文件中读取 JSON 数据
            String filePath = "E:\\steam-scraper\\output_detail.json"; // 请替换为你的文件路径
            String jsonFileContent = readFromFile(filePath);

            String[] jsonObjects = jsonFileContent.split("\n");

            for (String jsonObject : jsonObjects) {
                GameRoundPic game = convertJsonToRoundGame(objectMapper.readValue(jsonObject, Map.class));
                gameList.add(game);
            }
            // 打印结果
            for (GameRoundPic game : gameList) {
                System.out.println(game);
            }
            List<GameRoundPic> distinctList = new ArrayList<>(
                    gameList.stream().collect(Collectors.toMap(GameRoundPic::getId, obj -> obj, (existing, replacement) -> existing)).values()
            );
            gameRoundPicService.saveBatch(distinctList);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static GameRoundPic convertJsonToRoundGame(Map<String, Object> jsonMap) {
        GameRoundPic game = new GameRoundPic();
        String url = (String) jsonMap.get("game_urls");
        String[] split = url.split("/");
        String id = split[split.length - 2];
        game.setId(Long.valueOf(id));
        game.setName((String) jsonMap.get("game_name"));
        game.setUrl((String) jsonMap.get("game_urls"));
        game.setSteamImages((List<String>) jsonMap.get("game_pic"));
        return game;
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

    @Test
    public void testArray() {
        int[] array = {1, 2, 4};
        String string = Arrays.toString(array);
        System.out.println(string);
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
            game.setGenres(Arrays.toString(genres.toArray()));
        }
        game.setDeveloper((String) jsonMap.get("developer"));
        game.setPublisher((String) jsonMap.get("publisher"));
        String releaseDate = (String) jsonMap.get("release_date");
        game.setReleaseDate(releaseDate);
        game.setAppName((String) jsonMap.get("app_name"));
        List<String> tags = (List<String>) jsonMap.get("tags");
        if (tags != null) {
            game.setTags(Arrays.toString(tags.toArray()));
        }
        String discount = (String) jsonMap.get("discount_price");
        if (discount != null) {
            game.setDiscountPrice(discount);

        }

//        game.setPrice(Integer.valueOf((String) jsonMap.get("price")));

        String obj = (String) jsonMap.get("price");
        if (obj != null) {
            if (obj.contains("Free")) {
                game.setPrice("0");
            } else {
                String[] s = obj.split(" ");
                try {
                    game.setUnit(s[0]);
                    game.setPrice(s[1]);
                } catch (Exception e) {
                    System.out.println("出错的数据是" + Arrays.toString(s));
                }

            }

        }
        game.setSentiment((String) jsonMap.get("sentiment"));
        game.setEarlyAccess((Boolean) jsonMap.get("early_access"));
        return game;
    }

    private static GameShow convertJsonToGameShow(Map<String, Object> jsonMap) {
        GameShow game = new GameShow();
        game.setId(Long.valueOf((String) jsonMap.get("id")));
        game.setName((String) jsonMap.get("game_name"));
        game.setUrl((String) jsonMap.get("game_url"));
        game.setSteamPic((String) jsonMap.get("game_pic"));
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
