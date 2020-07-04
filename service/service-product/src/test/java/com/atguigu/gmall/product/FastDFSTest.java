package com.atguigu.gmall.product;

import lombok.extern.slf4j.Slf4j;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sound.midi.Soundbank;
import java.io.IOException;

/**
 * FastDFSTest
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-03
 * @Description:
 */
@Slf4j
public class FastDFSTest {


    @Test
    public void fastDFSTest() {

        try {

            // 1. 读取FastDFS配置文件
            String path = FastDFSTest.class.getClassLoader().getResource("tracker.conf").getPath();
            log.info(path);
            ClientGlobal.init(path);

            // 2. 建立tracker连接
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer connection = trackerClient.getConnection();

            // 3. 创建storage连接
            StorageClient storageClient = new StorageClient(connection,null);

            // 4. 上传文件
            String[] jpgs = storageClient.upload_file("D:\\img\\2.jpg", "jpg", null);

            String imgUrl = "http://192.168.200.128:8080/";

            for (String jpg : jpgs) {
                imgUrl = imgUrl + "/" + jpg;
            }

            System.out.println(imgUrl);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }

    }
}
