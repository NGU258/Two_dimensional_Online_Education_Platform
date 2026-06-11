package com.edu.platform.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LearningProgress {
    private Long id;
    private Long userId; // 用户 ID
    private Long courseId; // 课程 ID
    private Integer progress; // 学习进度（0-100）
    private LocalDateTime lastLearnTime; // 最后学习时间
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}