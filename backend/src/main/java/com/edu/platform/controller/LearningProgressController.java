package com.edu.platform.controller;

import com.edu.platform.model.LearningProgress;
import com.edu.platform.service.LearningProgressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/learningProgress")
@Api(tags = "学习进度管理", description = "学习进度的创建、更新、查询等操作")
public class LearningProgressController {

    @Autowired
    private LearningProgressService learningProgressService;

    @PostMapping("/create")
    @ApiOperation(value = "创建学习进度", notes = "创建新的学习进度，需要提供用户ID、课程ID和进度值")
    public Map<String, Object> create(@ApiParam(name = "learningProgress", value = "学习进度信息", required = true) @RequestBody LearningProgress learningProgress) {
        Map<String, Object> result = new HashMap<>();
        try {
            LearningProgress createdProgress = learningProgressService.create(learningProgress);
            result.put("code", 200);
            result.put("message", "创建学习进度成功");
            result.put("data", createdProgress);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "创建学习进度失败: " + e.getMessage());
        }
        return result;
    }

    @PutMapping("/update")
    @ApiOperation(value = "更新学习进度", notes = "更新学习进度，需要提供学习进度ID和更新的进度值")
    public Map<String, Object> update(@ApiParam(name = "learningProgress", value = "学习进度信息", required = true) @RequestBody LearningProgress learningProgress) {
        Map<String, Object> result = new HashMap<>();
        try {
            LearningProgress updatedProgress = learningProgressService.update(learningProgress);
            result.put("code", 200);
            result.put("message", "更新学习进度成功");
            result.put("data", updatedProgress);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "更新学习进度失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/get/{userId}/{courseId}")
    @ApiOperation(value = "获取学习进度", notes = "根据用户ID和课程ID获取学习进度")
    public Map<String, Object> getByUserIdAndCourseId(@ApiParam(name = "userId", value = "用户ID", required = true) @PathVariable Long userId, @ApiParam(name = "courseId", value = "课程ID", required = true) @PathVariable Long courseId) {
        Map<String, Object> result = new HashMap<>();
        try {
            LearningProgress progress = learningProgressService.getByUserIdAndCourseId(userId, courseId);
            if (progress != null) {
                result.put("code", 200);
                result.put("message", "获取学习进度成功");
                result.put("data", progress);
            } else {
                result.put("code", 404);
                result.put("message", "学习进度不存在");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取学习进度失败: " + e.getMessage());
        }
        return result;
    }
}