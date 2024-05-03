package com.Cang.utils;

import java.io.*;

/**
 * @author Jdfcc
 * @Description FileUtil
 * @DateTime 2023/6/26 14:39
 */
public class FileUtil {

    /**
     * 将文件以固定格式写入
     *
     * @param path     文件写入路径,需以/结尾
     * @param filename 文件名
     * @param value    写入的内容
     * @param suffix   文件扩展名
     */
    public static void writeFile(String path, String filename, String value, String suffix) {

        String logFile = path + filename + "." + suffix;
        try {
            File file = new File(logFile);
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file, true);
            fileWriter.write(value + "\n");
            fileWriter.close();
            System.out.println("Content appended to file successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    /**
     * 阅读文本类型的文件并返回其内容.
     * @param path textFile path
     * @return 文本内容
     */
    public static String readTextFile(String path) throws IOException{
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

}
