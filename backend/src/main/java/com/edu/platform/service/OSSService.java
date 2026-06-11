package com.edu.platform.service;

import org.springframework.web.multipart.MultipartFile;

public interface OSSService {
    /**
     * 上传文件到OSS
     * @param file 上传的文件
     * @param directory 存储目录
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file, String directory);

    /**
     * 删除OSS上的文件
     * @param filePath 文件路径
     */
    void deleteFile(String filePath);
}
