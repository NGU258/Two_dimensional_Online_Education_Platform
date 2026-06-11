package com.edu.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.edu.platform.mapper.LearningProgressMapper;
import com.edu.platform.model.LearningProgress;
import com.edu.platform.service.LearningProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LearningProgressServiceImpl implements LearningProgressService {

    @Autowired
    private LearningProgressMapper learningProgressMapper;

    @Override
    public LearningProgress create(LearningProgress learningProgress) {
        learningProgress.setProgress(0);
        learningProgress.setLastLearnTime(LocalDateTime.now());
        learningProgress.setCreateTime(LocalDateTime.now());
        learningProgress.setUpdateTime(LocalDateTime.now());
        learningProgressMapper.insert(learningProgress);
        return learningProgress;
    }

    @Override
    public LearningProgress update(LearningProgress learningProgress) {
        learningProgress.setLastLearnTime(LocalDateTime.now());
        learningProgress.setUpdateTime(LocalDateTime.now());
        learningProgressMapper.updateById(learningProgress);
        return learningProgress;
    }

    @Override
    public LearningProgress getByUserIdAndCourseId(Long userId, Long courseId) {
        QueryWrapper<LearningProgress> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("course_id", courseId);
        return learningProgressMapper.selectOne(wrapper);
    }
}