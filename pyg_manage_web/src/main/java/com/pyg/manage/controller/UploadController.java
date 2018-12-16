package com.pyg.manage.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

/**
 * @author xxx
 * @date 2018/11/29 20:12
 * @description
 */
@RestController
public class UploadController {
    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("upload")
    public Result upload(MultipartFile file){
        String extName = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf(".") + 1);
        System.out.println(extName);

        try {

            FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
            String path = client.uploadFile(file.getBytes(), extName, null);


            return new Result(true,FILE_SERVER_URL+path);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }
}
