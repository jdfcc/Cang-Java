package com.Cang.service.impl;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description TODO
 * @DateTime 2023/9/5 11:05
 */
import java.io.*;
import java.util.regex.*;
import java.util.ArrayList;
import java.util.List;

public class NewVttToLrcConverter {
    public static void main(String[] args) {
        String inputFilePath = "C:\\Users\\Jdfcc\\Downloads\\114524\\RJ01078070\\音聲\\123\\續・被在迷途森林遇見的年上精靈給嬌寵疼愛一整晚的故事.wav.vtt"; // 输入VTT文件的路径
        String outputFilePath = "C:\\Users\\Jdfcc\\Downloads\\114524\\RJ01078070\\音聲\\123\\續・被在迷途森林遇見的年上精靈給嬌寵疼愛一整晚的故事.wav.vtt"; // 输出LRC文件的路径

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));
            String line;

            boolean textStarted = false;

            while ((line = reader.readLine()) != null) {
                if (textStarted) {
                    // Write the text content as LRC
                    writer.write(line);
                    writer.newLine();
                } else if (line.trim().isEmpty()) {
                    textStarted = true; // Start writing text content
                }
            }

            reader.close();
            writer.close();

            System.out.println("Conversion from VTT to LRC completed successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
