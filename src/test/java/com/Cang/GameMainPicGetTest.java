package com.Cang;


import cn.hutool.core.collection.CollectionUtil;
import com.Cang.entity.GameRoundPic;
import com.Cang.entity.GameShow;
import com.Cang.service.GameShowService;
import com.Cang.service.impl.MinIOFileStorageService;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description GameMainPicGet，遍历游戏json获取并下载游戏图片
 * @DateTime 2024/1/11 16:27
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameMainPicGetTest {
    @Autowired
    public MinIOFileStorageService minIoFileStorageService;
    @Autowired
    public GameShowService gameShowService;
    private static final String SAVE_PATH = "D:\\resource\\detailPic\\";
    private static final String SAVE_PATH_ROUND = "D:\\resource\\roundPic\\";
    private static final String FILE_PREFIX = "Games/";


    /**
     * 根据url下载图片至本地
     *
     * @param targetUr 要下载的图片url
     * @param filePath 保存路径
     * @param dir      要保存的目录
     * @param filename 保存的文件名
     */
    private void downloadPicture(String targetUr, String filePath, String dir, String filename) {
        try {
            URL url = new URL(targetUr);

            // 获取URLConnection以便获取文件类型
            URLConnection connection = url.openConnection();
            String contentType = connection.getContentType();

            // 获取文件后缀
            String fileExtension = "";
            if (contentType != null && contentType.contains("/")) {
                fileExtension = contentType.substring(contentType.lastIndexOf("/") + 1);
            }

            // 创建游戏文件夹
            String gameFolderPath = filePath + dir + "/";
            File gameFolder = new File(gameFolderPath);
            if (!gameFolder.exists()) {
                gameFolder.mkdirs(); // 创建目录，包括父目录
            }

            // 创建带后缀的文件名
            String fileNameWithExtension = filename + "." + fileExtension;

            // 创建输出文件
            FileOutputStream fileOutputStream = new FileOutputStream(new File(gameFolderPath + fileNameWithExtension));

            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDownload() {
        downloadPicture("https://cdn.akamai.steamstatic.com/steam/apps/202200/capsule_616x353.jpg?t=1693335277",
                "C:\\Users\\Jdfcc\\Pictures\\", "EverQuest_II", "EverQuest_II");
        writeToLocal(new ArrayList<GameShow>() {{
            add(new GameShow() {{
                setId(1L);
                setMyPic("http://");
                setUrl("http://");
                setName("C:\\Users\\Jdfcc\\Pictures\\");
                setSteamPic(null);
            }});
        }}, "C:\\Users\\Jdfcc\\Desktop\\steam-scraper\\steam\\successgames.json");

    }


    /**
     * TODO
     * 通过此方法将抓取到的游戏首页图片下载至本地
     */
    @Test
    public void getPics() throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        List<GameShow> gameList = new ArrayList<>();

        // 从文件中读取 JSON 数据
        String filePath = "C:\\Users\\Jdfcc\\Desktop\\steam-scraper\\output.json"; // 请替换为你的文件路径
        String jsonFileContent = readFromFile(filePath);

        String[] jsonObjects = jsonFileContent.split("\n");

        for (String jsonObject : jsonObjects) {
            GameShow game = convertJsonToGame(objectMapper.readValue(jsonObject, Map.class));
            gameList.add(game);
        }


        ArrayList<GameShow> errorGames = new ArrayList<>();
        ArrayList<GameShow> successGames = new ArrayList<>();
        // 下载图片到本地
        for (GameShow game : gameList) {
            String url = game.getSteamPic();
            try {
                String name = game.getName();
                downloadPicture(url, SAVE_PATH, name, name);

                String gameFolderPath = SAVE_PATH + name + "/";
                // 上传图片到minio
                FileInputStream fileInputStream =
                        new FileInputStream(gameFolderPath + name + ".jpg");

                String res = minIoFileStorageService.
                        uploadImgFile(FILE_PREFIX + name + "/", name + ".jpg", fileInputStream);

                game.setMyPic(res);
                successGames.add(game);
                fileInputStream.close();
            } catch (Exception e) {
                errorGames.add(game);
                System.out.println("Error downloading" + game.getName());
            }
        }
        writeToLocal(errorGames, "C:\\Users\\Jdfcc\\Desktop\\steam-scraper\\steam\\errorgames.json");
        writeToLocal(successGames, "C:\\Users\\Jdfcc\\Desktop\\steam-scraper\\steam\\successgames.json");
        gameShowService.saveBatch(successGames);
    }

    private static ArrayList<String> convertToArrayList(String imageUrlData) {
        // Remove brackets and split the string by ","
        String[] imageUrlArray = imageUrlData.substring(1, imageUrlData.length() - 1).split(", ");

        // Convert the array to ArrayList
        ArrayList<String> imageUrlList = new ArrayList<>();
        Collections.addAll(imageUrlList, imageUrlArray);
        return imageUrlList;
    }

    /**
     * 获取游戏轮播图上传并写入本地
     */
    @Test
    public void getRoundPic() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<GameRoundPic> gameList = new ArrayList<>();

        // 从文件中读取 JSON 数据
        String filePath = "C:\\Users\\Jdfcc\\Desktop\\steam-scraper\\output_detail.json"; // 请替换为你的文件路径
        String jsonFileContent = readFromFile(filePath);

        String[] jsonObjects = jsonFileContent.split("\n");

        for (String jsonObject : jsonObjects) {
            GameRoundPic game = convertJsonToRoundGame(objectMapper.readValue(jsonObject, Map.class));
            gameList.add(game);
        }

        for (GameRoundPic game : gameList) {
            String steamImages = game.getSteamImages();
//            List<String> list = Collections.singletonList(steamImages);
            ArrayList<String> list = convertToArrayList(steamImages);
            int index = 1;
            if(!CollectionUtil.isEmpty(list)){
                for (String steamImage : list) {
                    if(steamImage.isEmpty()){
                        continue;
                    }
                    String fileExtension = getFileNameWithExtension(steamImage);
                    // TODO
//                downloadPicture(steamImage,FILE_PREFIX,)
                    String fileName = game.getName() + "_" + index;
                    downloadPicture(steamImage, SAVE_PATH, game.getName(), fileName);

                    String gameFolderPath = SAVE_PATH + game.getName() + "/";

                    // 上传图片到minio
                    FileInputStream fileInputStream =
                            new FileInputStream(gameFolderPath + fileName + fileExtension);

                    String res = minIoFileStorageService.
                            uploadImgFile(FILE_PREFIX + game.getName() + "/", fileName + fileExtension, fileInputStream);
                    index += 1;
                    System.out.println(res);
                }

            }
        }
    }

    /**
     * 拿到文件后缀
     *
     * @param fileUrl 要获取后缀的url
     * @return 例如 ".jpg",".mp3"
     */
    public static String getFileNameWithExtension(String fileUrl) throws IOException {
        URL url = new URL(fileUrl);

        // 获取URLConnection以便获取文件类型
        URLConnection connection = url.openConnection();
        String contentType = connection.getContentType();

        // 获取文件后缀
        String fileExtension = "";
        if (contentType != null && contentType.contains("/")) {
            fileExtension = contentType.substring(contentType.lastIndexOf("/") + 1);
        }
        return "." + fileExtension;
    }

    private static String readFromFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content.toString();
    }

    private static GameShow convertJsonToGame(Map<String, Object> jsonMap) {
        GameShow game = new GameShow();
        game.setId(Long.valueOf((String) jsonMap.get("id")));
        game.setName((String) jsonMap.get("game_name"));
        game.setUrl((String) jsonMap.get("game_url"));
        game.setSteamPic((String) jsonMap.get("game_pic"));
        return game;
    }

    private static GameRoundPic convertJsonToRoundGame(Map<String, Object> jsonMap) {
        GameRoundPic game = new GameRoundPic();
        game.setId(Long.valueOf((String) jsonMap.get("id")));
        game.setName((String) jsonMap.get("game_name"));
        game.setUrl((String) jsonMap.get("game_urls"));
        game.setSteamImages((List<String>) jsonMap.get("game_pic"));
        return game;
    }


    public static void writeToLocal(List<GameShow> mainGames, String path) {
        ObjectMapper mapper = new ObjectMapper();
        try (
                FileWriter fileWriter = new FileWriter(path)) {
            JsonGenerator jsonGenerator = mapper.getFactory().createGenerator(fileWriter);
//            jsonGenerator.useDefaultPrettyPrinter(); // 使其格式化输出更易读
            for (GameShow game : mainGames) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("id", String.valueOf(game.getId()));
                jsonGenerator.writeStringField("game_name", game.getName());
                jsonGenerator.writeStringField("game_url", game.getUrl());
                jsonGenerator.writeStringField("game_pic", game.getSteamPic());
                jsonGenerator.writeStringField("my_pic", game.getMyPic());
                jsonGenerator.writeEndObject();
                jsonGenerator.writeRaw("\n"); // 在每行之间添加换行符
            }

            jsonGenerator.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
