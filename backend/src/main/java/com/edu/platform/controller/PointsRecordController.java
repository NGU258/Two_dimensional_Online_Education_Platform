package com.edu.platform.controller;

import com.edu.platform.model.PointsRecord;
import com.edu.platform.service.PointsRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/points")
@Api(tags = "积分管理", description = "积分的查询和添加操作")
public class PointsRecordController {
    @Autowired
    private PointsRecordService pointsRecordService;

    @GetMapping("/user/{userId}")
    @ApiOperation(value = "获取用户积分记录", notes = "根据用户ID获取用户的积分记录列表")
    public List<PointsRecord> getByUserId(@ApiParam(name = "userId", value = "用户ID", required = true) @PathVariable Long userId) {
        return pointsRecordService.getByUserId(userId);
    }

    @GetMapping("/total/{userId}")
    @ApiOperation(value = "获取用户总积分", notes = "根据用户ID获取用户的总积分")
    public Integer getTotalPoints(@ApiParam(name = "userId", value = "用户ID", required = true) @PathVariable Long userId) {
        return pointsRecordService.getTotalPoints(userId);
    }

    @PostMapping
    @ApiOperation(value = "添加积分", notes = "为用户添加积分")
    public boolean addPoints(@ApiParam(name = "userId", value = "用户ID", required = true) @RequestParam Long userId, @ApiParam(name = "points", value = "积分值", required = true) @RequestParam Integer points, @ApiParam(name = "type", value = "积分类型", required = true) @RequestParam String type, @ApiParam(name = "description", value = "积分描述", required = true) @RequestParam String description) {
        return pointsRecordService.addPoints(userId, points, type, description);
    }
}
