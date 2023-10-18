package com.Cang.service.impl;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description TODO
 * @DateTime 2023/9/5 11:03
 */
import java.io.*;
import java.util.regex.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class VttToLrcConverter {
    public static void main(String[] args) {
        String inputFolderPath = "C:\\Users\\Jdfcc\\Downloads\\114524\\RJ01078070\\音聲\\123"; // 输入文件夹的路径
        String outputFolderPath = "C:\\Users\\Jdfcc\\Downloads\\114524\\RJ01078070\\音聲\\123"; // 输出文件夹的路径

        File inputFolder = new File(inputFolderPath);

        if (!inputFolder.exists() || !inputFolder.isDirectory()) {
            System.err.println("Input folder does not exist or is not a directory.");
            return;
        }

        File[] files = inputFolder.listFiles();
        if (files == null) {
            System.err.println("Error listing files in the input folder.");
            return;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(".vtt")) {
                convertVttToLrc(file, outputFolderPath);
                System.out.println("Conversion from VTT to LRC completed for: " + file.getName());
            }
        }

        System.out.println("All VTT files in the folder have been converted to LRC.");
    }

    private static void convertVttToLrc(File inputFile, String outputFolderPath) {
        try {
            String fileNameWithoutExtension = inputFile.getName().replaceFirst("[.][^.]+$", "");
            String outputFilePath = outputFolderPath + File.separator + fileNameWithoutExtension + ".lrc";

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));
            String line;

            List<String> lrcLines = new ArrayList<>();
            Pattern timePattern = Pattern.compile("(\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{3}) --> (\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{3})");
            StringBuilder lrcLineBuilder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                Matcher matcher = timePattern.matcher(line);
                if (matcher.matches()) {
                    // Extract timestamps and convert to LRC format
                    lrcLineBuilder.setLength(0);
                    lrcLineBuilder.append("[");
                    lrcLineBuilder.append(line.substring(0, 8)); // Extract the start timestamp
                    lrcLineBuilder.append("]");
                    lrcLineBuilder.append(line.substring(29)); // Extract the text content
                    lrcLines.add(lrcLineBuilder.toString());
                }
            }

            // Write LRC lines to the output file
            for (String lrcLine : lrcLines) {
                writer.write(lrcLine);
                writer.newLine();
            }
            /**
             * 帮我写一段java代码将vtt格式的歌词文件转化为lrc格式的歌词文件.我需要转换的并不是一个vtt文件，而是某个文件夹里面的vtt文件，需要注意的是这个文件夹并不只有vtt文件，还会有其他格式的文件，你需要判断是不是vtt文件，是的话就转化为lrc文件，不是的话就不需要转化.
             */

            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
