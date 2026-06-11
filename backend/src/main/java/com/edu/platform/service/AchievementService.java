package com.edu.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.platform.model.Achievement;

import java.util.List;

public interface AchievementService extends IService<Achievement> {
    List<Achievement> getByUserId(Long userId);
    List<Achievement> getByType(Long userId, String achievementType);
    boolean addAchievement(Long userId, String achievementName, String description, String achievementType);
}
