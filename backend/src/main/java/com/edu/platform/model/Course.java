package com.edu.platform.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Course {
    private Long id;
    private String title;
    private String description;
    private String cover;
    private String videoUrl;
    private Integer duration; // 课程时长（分钟）
    private Integer categoryId; // 课程分类 ID
    private Integer teacherId; // 教师 ID
    private Integer level; // 课程难度（1-初级，2-中级，3-高级）
    private Integer status; // 课程状态（0-禁用，1-启用）
    private Integer viewCount; // 观看次数
    private Integer likeCount; // 点赞次数
    private Integer commentCount; // 评论次数
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}