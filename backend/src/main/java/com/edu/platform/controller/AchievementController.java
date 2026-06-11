package com.edu.platform.controller;

import com.edu.platform.model.Achievement;
import com.edu.platform.service.AchievementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
@Api(tags = "成就管理", description = "成就的查询和添加操作")
public class AchievementController {
    @Autowired
    private AchievementService achievementService;

    @GetMapping("/user/{userId}")
    @ApiOperation(value = "获取用户成就", notes = "根据用户ID获取用户的成就列表")
    public List<Achievement> getByUserId(@ApiParam(name = "userId", value = "用户ID", required = true) @PathVariable Long userId) {

        return achievementService.getByUserId(userId);
    }

    @GetMapping("/user/{userId}/type/{achievementType}")
    @ApiOperation(value = "获取用户指定类型成就", notes = "根据用户ID和成就类型获取成就列表")
    public List<Achievement> getByType(@ApiParam(name = "userId", value = "用户ID", required = true) @PathVariable Long userId,
                                       @ApiParam(name = "achievementType", value = "成就类型", required = true) @PathVariable String achievementType) {
        return achievementService.getByType(userId, achievementType);
    }

    @PostMapping
    @ApiOperation(value = "添加成就", notes = "为用户添加新的成就")
    public boolean addAchievement(@ApiParam(name = "userId", value = "用户ID", required = true) @RequestParam Long userId,
                                 @ApiParam(name = "achievementName", value = "成就名称", required = true) @RequestParam String achievementName,
                                 @ApiParam(name = "description", value = "成就描述", required = true) @RequestParam String description,
                                 @ApiParam(name = "achievementType", value = "成就类型", required = false) @RequestParam(required = false, defaultValue = "learning") String achievementType) {
        return achievementService.addAchievement(userId, achievementName, description, achievementType);
    }
}
