
package com.Cang.service.impl;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BatchVttToLrcConverter {
    public static void main(String[] args) throws FileNotFoundException {
        String inputFolder = "C:\\Users\\Jdfcc\\Downloads\\114524\\RJ01077605 - 副本"; // 输入文件夹路径
        String outputFolder = "C:\\Users\\Jdfcc\\Downloads\\114524\\RJ01077605 - 副本"; // 输出文件夹路径

        File inputDir = new File(inputFolder);
        File[] vttFiles = inputDir.listFiles((dir, name) -> name.endsWith(".vtt"));

        if (vttFiles == null) {
            System.err.println("无法读取输入文件夹或没有找到.vtt文件。");
            return;
        }

        for (File vttFile : vttFiles) {
            convertVttToLrc(vttFile, outputFolder);
        }

        System.out.println("批量转换完成。");
    }

    private static void convertVttToLrc(File vttFile, String outputFolder) throws FileNotFoundException {
        try (BufferedReader reader = new BufferedReader(new FileReader(vttFile))) {
            String lrcFileName = vttFile.getName().replace(".vtt", ".lrc");
            File lrcFile = new File(outputFolder, lrcFileName);

            boolean isFirstLineProcessed = false;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(lrcFile))) {
                String line;
                String currentTime = "";
                Pattern timePattern = Pattern.compile("(\\d{2}:\\d{2}:\\d{2}\\.\\d{3}) -->");

                // 处理第一行
                line = reader.readLine();
                if (line != null && line.equals("WEBVTT")) {
                    isFirstLineProcessed = true;
                } else if (line != null) {
                    processLine(isFirstLineProcessed,line, currentTime, writer, timePattern);
                }

                // 继续处理剩余所有行
                while ((line = reader.readLine()) != null) {
                    processLine(isFirstLineProcessed,line, currentTime, writer, timePattern);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 辅助方法：处理每一行内容

    }

    private static void processLine (Boolean isFirstLineProcessed, String line, String currentTime, BufferedWriter writer, Pattern timePattern) throws
            IOException {
        if (line.matches("\\d+")) {
            // 如果行只包含数字，跳过该行（标识段落的数字）
            return;
        } else if (line.contains("-->")) {
            // 匹配VTT格式的时间戳，提取第一个时间
            Matcher matcher = timePattern.matcher(line);
            if (matcher.find()) {
                currentTime = "[" + matcher.group(1) + "]";
            }
        } else if (!line.isEmpty()) {
            // 写入LRC格式的歌词行（只有在不是首行或首行已被处理的情况下才写入）
            if (isFirstLineProcessed || !line.equals("WEBVTT")) {
                writer.write(currentTime + line + "\n");
            }
        }
    }
}

