package com.edu.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.platform.mapper.PointsRecordMapper;
import com.edu.platform.model.PointsRecord;
import com.edu.platform.service.PointsRecordService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PointsRecordServiceImpl extends ServiceImpl<PointsRecordMapper, PointsRecord> implements PointsRecordService {
    @Override
    public List<PointsRecord> getByUserId(Long userId) {
        return baseMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<PointsRecord>().eq("user_id", userId));
    }

    @Override
    public boolean addPoints(Long userId, Integer points, String type, String description) {
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setPoints(points);
        record.setType(type);
        record.setDescription(description);
        record.setCreateTime(new Date());
        return save(record);
    }

    @Override
    public Integer getTotalPoints(Long userId) {
        List<PointsRecord> records = getByUserId(userId);
        return records.stream().mapToInt(PointsRecord::getPoints).sum();
    }
}
