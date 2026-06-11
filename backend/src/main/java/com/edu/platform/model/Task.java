package com.edu.platform.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("task")
public class Task {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String taskType; // 任务类型：main(主线), daily(每日), weekly(周常), challenge(挑战), hidden(隐藏)
    private String taskName;
    private String description;
    private String status; // 状态：pending(待完成), in_progress(进行中), completed(已完成), claimed(已领取奖励)
    private Integer progress; // 任务进度
    private Integer targetProgress; // 目标进度
    private String reward; // 奖励
    private Date startTime; // 任务开始时间
    private Date endTime; // 任务结束时间
    private Date completedTime; // 完成时间
    private Date createTime;
    private Date updateTime;
}