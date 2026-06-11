package com.edu.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.platform.mapper.TaskMapper;
import com.edu.platform.model.Task;
import com.edu.platform.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
    
    @Override
    public List<Task> getTasksByUserId(Long userId) {
        QueryWrapper<Task> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return baseMapper.selectList(wrapper);
    }
    
    @Override
    public List<Task> getTasksByType(Long userId, String taskType) {
        QueryWrapper<Task> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("task_type", taskType);
        return baseMapper.selectList(wrapper);
    }
    
    @Override
    public boolean updateTaskProgress(Long taskId, Integer progress) {
        Task task = baseMapper.selectById(taskId);
        if (task == null) {
            return false;
        }
        
        task.setProgress(progress);
        if (progress >= task.getTargetProgress()) {
            task.setStatus("completed");
            task.setCompletedTime(new Date());
        } else if (task.getStatus().equals("pending")) {
            task.setStatus("in_progress");
        }
        
        return baseMapper.updateById(task) > 0;
    }
    
    @Override
    public boolean claimTaskReward(Long taskId) {
        Task task = baseMapper.selectById(taskId);
        if (task == null || !task.getStatus().equals("completed")) {
            return false;
        }
        
        task.setStatus("claimed");
        return baseMapper.updateById(task) > 0;
    }
    
    @Override
    public Task createTask(Long userId, String taskType, String taskName, String description, 
                          Integer targetProgress, String reward, Date startTime, Date endTime) {
        Task task = new Task();
        task.setUserId(userId);
        task.setTaskType(taskType);
        task.setTaskName(taskName);
        task.setDescription(description);
        task.setStatus("pending");
        task.setProgress(0);
        task.setTargetProgress(targetProgress);
        task.setReward(reward);
        task.setStartTime(startTime);
        task.setEndTime(endTime);
        task.setCreateTime(new Date());
        task.setUpdateTime(new Date());
        
        baseMapper.insert(task);
        return task;
    }
    
    @Override
    public void refreshDailyTasks(Long userId) {
        // 先删除已过期的每日任务
        QueryWrapper<Task> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("user_id", userId)
                    .eq("task_type", "daily")
                    .lt("end_time", new Date());
        baseMapper.delete(deleteWrapper);
        
        // 创建新的每日任务
        Date now = new Date();
        Date endOfDay = new Date(now.getTime() + 24 * 60 * 60 * 1000);
        
        // 示例每日任务
        createTask(userId, "daily", "每日登录", "每天登录平台", 1, "10经验值", now, endOfDay);
        createTask(userId, "daily", "观看课程", "观看10分钟课程", 10, "15经验值", now, endOfDay);
        createTask(userId, "daily", "完成练习", "完成1道练习题", 1, "20经验值", now, endOfDay);
    }
    
    @Override
    public void refreshWeeklyTasks(Long userId) {
        // 先删除已过期的周常任务
        QueryWrapper<Task> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("user_id", userId)
                    .eq("task_type", "weekly")
                    .lt("end_time", new Date());
        baseMapper.delete(deleteWrapper);
        
        // 创建新的周常任务
        Date now = new Date();
        Date endOfWeek = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000);
        
        // 示例周常任务
        createTask(userId, "weekly", "周学习时长", "累计学习60分钟", 60, "50经验值", now, endOfWeek);
        createTask(userId, "weekly", "周课程数", "完成3门课程", 3, "60经验值", now, endOfWeek);
        createTask(userId, "weekly", "周练习数", "完成10道练习题", 10, "70经验值", now, endOfWeek);
    }
}