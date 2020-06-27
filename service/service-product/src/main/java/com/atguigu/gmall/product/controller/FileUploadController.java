package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * FileUploadController
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-03
 * @Description:
 */
@Api(tags = "文件上传管理")
@RestController
@RequestMapping("/admin/product")
@CrossOrigin
@Slf4j
public class FileUploadController {

    private static final String PREFIXURL = "http://192.168.200.128:8080";

    @ApiOperation("图片上传")
    @PostMapping("/fileUpload")
    public Result<String> fileUpload(MultipartFile file) {
        try {

            // 1. 读取FastDFS配置文件
            String path = FileUploadController.class.getClassLoader().getResource("tracker.conf").getPath();

            ClientGlobal.init(path);

            // 2. 建立tracker连接
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer connection = trackerClient.getConnection();

            // 3. 创建storage连接
            StorageClient storageClient = new StorageClient(connection,null);

            // 4. 上传文件
//            String originalFilename = file.getOriginalFilename();
//            String fileExtName = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileExtName = FilenameUtils.getExtension(file.getOriginalFilename());
            String[] jpgs = storageClient.upload_file(file.getBytes(), fileExtName, null);

            String imgUrl = PREFIXURL;

            for (String jpg : jpgs) {
                imgUrl = imgUrl + "/" + jpg;
            }
            log.info(imgUrl);
            return Result.ok(imgUrl);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }

        return null;
    }

}
