package com.Cang.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.Cang.dto.Result;
import com.Cang.constants.SystemConstants;
import com.Cang.service.impl.MinIOFileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

//    TODO 重写文件保存逻辑，判断文件是否存在，之后再传
    @PostMapping("/blog")
    public Result uploadImage(@RequestParam("file") MultipartFile image, HttpServletRequest request) {
        log.info("%%%%%%%%%% {}",request.getHeader("Content-Type"));
        try {
            // 获取原始文件名称
            String originalFilename = image.getOriginalFilename();
            // 生成新文件名
//            String fileName = createNewFileName(originalFilename);

            UUID uuid = UUID.randomUUID();
            String filename=uuid.toString()+".jpg";

            InputStream inputStream = image.getInputStream();
            String fileUrl = minIoFileStorageService.uploadImgFile("", filename, inputStream);
            // 保存文件
//            image.transferTo(new File(SystemConstants.IMAGE_UPLOAD_DIR, fileName));
            // 返回结果
            log.debug("文件上传成功，{}", fileUrl);
//            return Result.ok(fileName);
            return Result.ok(fileUrl);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
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
