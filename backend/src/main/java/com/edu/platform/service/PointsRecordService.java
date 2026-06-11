package com.edu.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.platform.model.PointsRecord;

import java.util.List;

public interface PointsRecordService extends IService<PointsRecord> {
    List<PointsRecord> getByUserId(Long userId);
    boolean addPoints(Long userId, Integer points, String type, String description);
    Integer getTotalPoints(Long userId);
}
