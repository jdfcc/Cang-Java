package com.Cang;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description FileDownloader
 * @DateTime 2024/3/25 13:07
 */
public class FileDownloader {
    private static final String FILE_PATH = "C:\\Users\\Jdfcc\\AppData\\Roaming\\Telegram Desktop\\ThornSin\\ThornSin_Data\\resources.assets.resS";  // 本地文件路径
    private static final String DOWNLOAD_PATH = "D:\\resources.resS";  // 下载到本地的路径
    private static volatile boolean isPaused = false;

    public static void main(String[] args) {
        try {
            downloadFile(FILE_PATH, DOWNLOAD_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 断点传输文件
     * @param bytesRead the bytes read from the file
     * @param path the path to the download file
     * @param downloadPath the path to the download file
     * @throws IOException 文件异常
     */
    static void continueDownloadFile(int bytesRead, String path, String downloadPath) throws IOException {
        InputStream inputStream = Files.newInputStream(Paths.get(path));
        OutputStream outputStream = Files.newOutputStream(Paths.get(downloadPath));
        byte[] buffer = new byte[1024];
        outputStream.write(buffer, 0, bytesRead);
//        totalBytesRead += bytesRead;
//        double progress = (double) totalBytesRead / fileSize * 100;
    }

    public static void downloadFile(String filePath, String downloadPath) throws IOException {
        Path sourcePath = Paths.get(filePath);
        Path targetPath = Paths.get(downloadPath);
        long fileSize = Files.size(sourcePath);

        Thread downloadThread = new Thread(() -> {
            try (InputStream inputStream = Files.newInputStream(sourcePath);
                 OutputStream outputStream = Files.newOutputStream(targetPath)) {
                byte[] buffer = new byte[1024];
                long totalBytesRead = 0;
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    if (!isPaused) {
                        outputStream.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;
                        double progress = (double) totalBytesRead / fileSize * 100;
                        System.out.printf("\rDownloading... %.2f%%", progress);
                    } else {
                        System.out.println("\nDownload paused.");
                    }
                }

                outputStream.flush();
                System.out.println("\nDownload completed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        downloadThread.start();
    }

    public static void pauseDownload() {
        isPaused = true;
    }

    public static void resumeDownload() {
        isPaused = false;
    }
}

