package com.edu.platform.controller;

import com.edu.platform.service.OSSService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@Api(tags = "文件上传", description = "文件上传到阿里云OSS")
public class UploadController {

    @Autowired
    private OSSService ossService;

    @PostMapping("/avatar")
    @ApiOperation(value = "上传头像", notes = "上传用户头像到OSS")
    public Map<String, Object> uploadAvatar(@ApiParam(name = "file", value = "头像文件", required = true) @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 上传到avatar目录
            String url = ossService.uploadFile(file, "avatar");
            result.put("code", 200);
            result.put("message", "上传成功");
            result.put("data", url);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "上传失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/file")
    @ApiOperation(value = "上传文件", notes = "上传文件到OSS")
    public Map<String, Object> uploadFile(
            @ApiParam(name = "file", value = "文件", required = true) @RequestParam("file") MultipartFile file,
            @ApiParam(name = "directory", value = "存储目录", required = false, defaultValue = "file") @RequestParam(value = "directory", defaultValue = "file") String directory) {
        Map<String, Object> result = new HashMap<>();
        try {
            String url = ossService.uploadFile(file, directory);
            result.put("code", 200);
            result.put("message", "上传成功");
            result.put("data", url);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "上传失败: " + e.getMessage());
        }
        return result;
    }
}
