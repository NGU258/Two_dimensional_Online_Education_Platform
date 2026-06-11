package com.edu.platform.service.impl;

import com.aliyun.oss.OSS;
import com.edu.platform.service.OSSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class OSSServiceImpl implements OSSService {

    @Autowired
    private OSS ossClient;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun.oss.domain}")
    private String domain;

    @Value("${aliyun.oss.dir.prefix}")
    private String dirPrefix;

    @Override
    public String uploadFile(MultipartFile file, String directory) {
        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + suffix;
            String objectName = dirPrefix + directory + "/" + fileName;

            // 上传文件
            ossClient.putObject(bucketName, objectName, file.getInputStream());

            // 返回文件访问URL
            return domain + "/" + objectName;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        // 从URL中提取对象名
        String objectName = filePath.replace(domain + "/", "");
        ossClient.deleteObject(bucketName, objectName);
    }
}
