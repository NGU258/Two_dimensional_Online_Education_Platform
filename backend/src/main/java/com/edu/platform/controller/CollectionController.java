package com.edu.platform.controller;

import com.edu.platform.model.Collection;
import com.edu.platform.service.CollectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collections")
@Api(tags = "收藏管理", description = "收藏的查询、添加和删除操作")
public class CollectionController {
    @Autowired
    private CollectionService collectionService;

    @GetMapping("/user/{userId}")
    @ApiOperation(value = "获取用户收藏", notes = "根据用户ID获取用户的收藏列表")
    public List<Collection> getByUserId(@ApiParam(name = "userId", value = "用户ID", required = true) @PathVariable Long userId) {
        return collectionService.getByUserId(userId);
    }

    @PostMapping
    @ApiOperation(value = "添加收藏", notes = "为用户添加课程收藏")
    public boolean addCollection(@ApiParam(name = "userId", value = "用户ID", required = true) @RequestParam Long userId, @ApiParam(name = "courseId", value = "课程ID", required = true) @RequestParam Long courseId) {
        return collectionService.addCollection(userId, courseId);
    }

    @DeleteMapping
    @ApiOperation(value = "删除收藏", notes = "删除用户的课程收藏")
    public boolean removeCollection(@ApiParam(name = "userId", value = "用户ID", required = true) @RequestParam Long userId, @ApiParam(name = "courseId", value = "课程ID", required = true) @RequestParam Long courseId) {
        return collectionService.removeCollection(userId, courseId);
    }

    @GetMapping("/check")
    @ApiOperation(value = "检查收藏状态", notes = "检查用户是否已收藏课程")
    public boolean isCollected(@ApiParam(name = "userId", value = "用户ID", required = true) @RequestParam Long userId, @ApiParam(name = "courseId", value = "课程ID", required = true) @RequestParam Long courseId) {
        return collectionService.isCollected(userId, courseId);
    }
}
