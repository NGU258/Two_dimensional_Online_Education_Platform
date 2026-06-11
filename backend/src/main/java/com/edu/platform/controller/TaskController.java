package com.edu.platform.controller;

import com.edu.platform.model.Task;
import com.edu.platform.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Api(tags = "任务管理", description = "任务的查询、更新和领取操作")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping("/user/{userId}")
    @ApiOperation(value = "获取用户任务列表", notes = "根据用户ID获取所有任务")
    public List<Task> getTasksByUserId(@ApiParam(name = "userId", value = "用户ID", required = true) @PathVariable Long userId) {
        return taskService.getTasksByUserId(userId);
    }

    @GetMapping("/user/{userId}/type/{taskType}")
    @ApiOperation(value = "获取用户指定类型任务", notes = "根据用户ID和任务类型获取任务")
    public List<Task> getTasksByType(@ApiParam(name = "userId", value = "用户ID", required = true) @PathVariable Long userId,
                                     @ApiParam(name = "taskType", value = "任务类型", required = true) @PathVariable String taskType) {
        return taskService.getTasksByType(userId, taskType);
    }

    @PutMapping("/progress/{taskId}")
    @ApiOperation(value = "更新任务进度", notes = "更新指定任务的进度")
    public boolean updateTaskProgress(@ApiParam(name = "taskId", value = "任务ID", required = true) @PathVariable Long taskId,
                                      @ApiParam(name = "progress", value = "任务进度", required = true) @RequestParam Integer progress) {
        return taskService.updateTaskProgress(taskId, progress);
    }

    @PutMapping("/claim/{taskId}")
    @ApiOperation(value = "领取任务奖励", notes = "领取指定任务的奖励")
    public boolean claimTaskReward(@ApiParam(name = "taskId", value = "任务ID", required = true) @PathVariable Long taskId) {
        return taskService.claimTaskReward(taskId);
    }

    @PostMapping("/refresh/daily/{userId}")
    @ApiOperation(value = "刷新每日任务", notes = "为用户刷新每日任务")
    public void refreshDailyTasks(@ApiParam(name = "userId", value = "用户ID", required = true) @PathVariable Long userId) {
        taskService.refreshDailyTasks(userId);
    }

    @PostMapping("/refresh/weekly/{userId}")
    @ApiOperation(value = "刷新周常任务", notes = "为用户刷新周常任务")
    public void refreshWeeklyTasks(@ApiParam(name = "userId", value = "用户ID", required = true) @PathVariable Long userId) {
        taskService.refreshWeeklyTasks(userId);
    }
}