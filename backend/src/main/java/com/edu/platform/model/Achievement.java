package com.edu.platform.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("achievement")
public class Achievement {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String achievementName;
    private String description;
    private String achievementType; // 成就类型：learning(学习), time(时间), social(社交), explore(探索), challenge(挑战)
    private Date obtainTime;
    private Date createTime;
    private Date updateTime;
}
