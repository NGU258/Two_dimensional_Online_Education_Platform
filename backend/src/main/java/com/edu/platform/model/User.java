package com.edu.platform.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String email;
    private String phone;
    private Integer role; // 0: 普通用户, 1: 教师, 2: 管理员
    private Integer status; // 0: 禁用, 1: 启用
    private Integer experience; // 经验值
    private Integer level; // 等级
    private Integer points; // 积分
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}