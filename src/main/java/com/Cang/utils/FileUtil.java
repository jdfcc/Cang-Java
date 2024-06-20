package com.Cang.utils;

import java.io.*;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;

/**
 * @author Jdfcc
 * @Description FileUtil
 * @DateTime 2023/6/26 14:39
 */
public  class FileUtil {

    public static String calculateMd5(File file) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(file);
       return calculateMd5(fileInputStream);
    }
    public static String calculateMd5(InputStream inputStream) throws Exception {
        // 创建MessageDigest实例用于计算MD5
        MessageDigest md = MessageDigest.getInstance("MD5");

        // 使用DigestInputStream包裹输入流
        try (DigestInputStream dis = new DigestInputStream(inputStream, md)) {
            byte[] buffer = new byte[1024];
            while (dis.read(buffer) != -1) {
                // 读取输入流，DigestInputStream会自动更新MessageDigest
            }
        }

        // 完成哈希计算
        byte[] md5Bytes = md.digest();

        // 将字节数组转换为16进制字符串
        BigInteger bigInt = new BigInteger(1, md5Bytes);
        return String.format("%032x", bigInt);
    }


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
