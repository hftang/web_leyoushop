package com.leyou.upload.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author hftang
 * @date 2019-01-17 20:30
 * @desc
 */
@Service
public class UploadService {

    private Logger logger = LoggerFactory.getLogger(UploadService.class);

    private final static List<String> allowTyps = Arrays.asList("image/png", "image/jpeg");

    public String uploadImage(MultipartFile file) {
        //1 先校验文件类型
        String contentType = file.getContentType();
        if (!allowTyps.contains(contentType)) {
            return null;
        }
        try {
            //2 判断 文件内容
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null) {
                return null;
            }
            //3 保存文件
            file.transferTo(new File("D:\\heima29\\upload", file.getOriginalFilename()));
            //4 生成图片地址
            String url = "http://image.leyou.com/upload/" + file.getOriginalFilename();

            return url;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("文件上传失败：文件名：{}", file.getOriginalFilename());
            return null;
        }
    }


}
