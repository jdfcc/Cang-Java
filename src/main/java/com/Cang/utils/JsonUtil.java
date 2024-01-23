package com.Cang.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description JsonUtil
 * @DateTime 2024/1/19 9:20
 */
public class JsonUtil {
    // TODO
//    public static void writeToLocal(List<Object> objects, String path) {
//        ObjectMapper mapper = new ObjectMapper();
//        try (FileWriter fileWriter = new FileWriter("C:\\Users\\Jdfcc\\Desktop\\steam-scraper\\games.json")) {
//            JsonGenerator jsonGenerator = mapper.getFactory().createGenerator(fileWriter);
////            jsonGenerator.useDefaultPrettyPrinter(); // 使其格式化输出更易读
//            for (Object obj : objects) {
//                jsonGenerator.writeStartObject();
//                jsonGenerator.writeStringField("game_name", game.game_name);
//                jsonGenerator.writeStringField("game_url", game.game_url);
//                jsonGenerator.writeStringField("game_pic", game.game_pic);
//                jsonGenerator.writeEndObject();
//                jsonGenerator.writeRaw("\n"); // 在每行之间添加换行符
//            }
//
//            jsonGenerator.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
