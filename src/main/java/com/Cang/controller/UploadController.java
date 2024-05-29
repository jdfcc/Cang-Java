package com.Cang.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.Cang.dto.Result;
import com.Cang.constants.SystemConstants;
import com.Cang.service.impl.MinIOFileStorageService;
import com.Cang.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author Jdfcc
 */
@Slf4j
@RestController
@RequestMapping("upload")
public class UploadController {
    @Autowired
    public MinIOFileStorageService minIoFileStorageService;

    private final Parser parser;
    private final HtmlRenderer renderer;

    @Autowired
    public UploadController() {
        this.parser = Parser.builder().build();
        this.renderer = HtmlRenderer.builder().build();
    }

    //    TODO 实现文件保存逻辑，判断文件是否存在，之后再传
    @PostMapping("/blog")
    public Result uploadJpg(@RequestParam("file") MultipartFile image, HttpServletRequest request) {
        try {
            UUID uuid = UUID.randomUUID();
            String filename = uuid+ ".jpg";
            InputStream inputStream = image.getInputStream();
            String fileUrl = minIoFileStorageService.uploadImgFile("", filename, inputStream);
            log.debug("文件上传成功，{}", fileUrl);
            return Result.ok(fileUrl);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

//    //    TODO
//    @PostMapping("/blog")
//    public Result uploadJpg(@RequestParam("file") MultipartFile image, HttpServletRequest request) {
//        try {
//            UUID uuid = UUID.randomUUID();
//            String filename = uuid+ ".jpg";
//            String md5 = com.Cang.utils.FileUtil.calculateMd5(image.getInputStream());
//            if(fileService.isFileUpload(md5)){
//                String url= fileService.getFileByMd5(md5);
//                return Result.ok(url);
//            }else{
//                InputStream inputStream = image.getInputStream();
//                String fileUrl = minIoFileStorageService.uploadImgFile("", filename, inputStream);
//                fileService.saveFile(UserHolder.getUser(), fileUrl,md5);
//                log.debug("文件上传成功，{}", fileUrl);
//                return Result.ok(fileUrl);
//            }
//
//        } catch (IOException e) {
//            throw new RuntimeException("文件上传失败", e);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    @PostMapping("/article")
    public Result uploadArticle(@RequestParam("file") MultipartFile article) throws IOException {
// 获取文件内容
        String markdownContent = new String(article.getBytes(), StandardCharsets.UTF_8);

        // 解析Markdown内容
        String htmlContent = renderer.render(parser.parse(markdownContent));
        return Result.ok(htmlContent);
    }

    @GetMapping("/blog/delete")
    public Result deleteBlogImg(@RequestParam("name") String filename) {
        File file = new File(SystemConstants.IMAGE_UPLOAD_DIR, filename);
        if (file.isDirectory()) {
            return Result.fail("错误的文件名称");
        }
        FileUtil.del(file);
        return Result.ok();
    }

    private String createNewFileName(String originalFilename) {
        // 获取后缀
        String suffix = StrUtil.subAfter(originalFilename, ".", true);
        // 生成目录
        String name = UUID.randomUUID().toString();
        int hash = name.hashCode();
        int d1 = hash & 0xF;
        int d2 = (hash >> 4) & 0xF;
        // 判断目录是否存在
        File dir = new File(SystemConstants.IMAGE_UPLOAD_DIR, StrUtil.format("/blogs/{}/{}", d1, d2));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 生成文件名
        return StrUtil.format("/blogs/{}/{}/{}.{}", d1, d2, name, suffix);
    }
}
