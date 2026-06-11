package com.edu.platform.controller;

import com.edu.platform.model.Comment;
import com.edu.platform.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@Api(tags = "评论管理", description = "评论的查询和添加操作")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping("/course/{courseId}")
    @ApiOperation(value = "获取课程评论", notes = "根据课程ID获取课程的评论列表")
    public List<Comment> getByCourseId(@ApiParam(name = "courseId", value = "课程ID", required = true) @PathVariable Long courseId) {
        return commentService.getByCourseId(courseId);
    }

    @GetMapping("/user/{userId}")
    @ApiOperation(value = "获取用户评论", notes = "根据用户ID获取用户的评论列表")
    public List<Comment> getByUserId(@ApiParam(name = "userId", value = "用户ID", required = true) @PathVariable Long userId) {
        return commentService.getByUserId(userId);
    }

    @PostMapping
    @ApiOperation(value = "添加评论", notes = "为课程添加评论")
    public boolean addComment(@ApiParam(name = "userId", value = "用户ID", required = true) @RequestParam Long userId, @ApiParam(name = "courseId", value = "课程ID", required = true) @RequestParam Long courseId, @ApiParam(name = "content", value = "评论内容", required = true) @RequestParam String content) {
        return commentService.addComment(userId, courseId, content);
    }
}
