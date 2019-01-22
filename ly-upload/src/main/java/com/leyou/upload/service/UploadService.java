package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.config.UploadProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author hftang
 * @date 2019-01-17 20:30
 * @desc
 */
@Service
@EnableConfigurationProperties(UploadProperties.class)
public class UploadService {

    private Logger logger = LoggerFactory.getLogger(UploadService.class);

    private final static List<String> allowTyps = Arrays.asList("image/png", "image/jpeg", "image/jpg");

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private UploadProperties prop;

    public String uploadImage(MultipartFile file) {
//        prop.
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
//            //3 保存文件
//            file.transferTo(new File("D:\\heima29\\upload", file.getOriginalFilename()));
//            //4 生成图片地址
//            String url = "http://image.leyou.com/upload/" + file.getOriginalFilename();

//            String extension= file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);

            System.out.println("---->最终返回url："+  "http://image.leyou.com/"+storePath.getFullPath());


            return "http://image.leyou.com/"+storePath.getFullPath();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("文件上传失败：文件名：{}", file.getOriginalFilename());
            return null;
        }
    }


}
