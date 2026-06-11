package com.edu.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.platform.model.Task;

import java.util.List;
import java.util.Date;

public interface TaskService extends IService<Task> {
    // 获取用户的任务列表
    List<Task> getTasksByUserId(Long userId);
    
    // 获取用户指定类型的任务
    List<Task> getTasksByType(Long userId, String taskType);
    
    // 更新任务进度
    boolean updateTaskProgress(Long taskId, Integer progress);
    
    // 领取任务奖励
    boolean claimTaskReward(Long taskId);
    
    // 为用户创建任务
    Task createTask(Long userId, String taskType, String taskName, String description, 
                    Integer targetProgress, String reward, Date startTime, Date endTime);
    
    // 刷新每日任务
    void refreshDailyTasks(Long userId);
    
    // 刷新周常任务
    void refreshWeeklyTasks(Long userId);
}