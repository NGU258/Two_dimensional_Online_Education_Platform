package com.edu.platform.service;

import com.edu.platform.model.LearningProgress;

public interface LearningProgressService {
    LearningProgress create(LearningProgress learningProgress);
    LearningProgress update(LearningProgress learningProgress);
    LearningProgress getByUserIdAndCourseId(Long userId, Long courseId);
}