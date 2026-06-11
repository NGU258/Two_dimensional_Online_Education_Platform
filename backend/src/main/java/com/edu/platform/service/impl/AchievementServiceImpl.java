package com.edu.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.platform.mapper.AchievementMapper;
import com.edu.platform.model.Achievement;
import com.edu.platform.service.AchievementService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AchievementServiceImpl extends ServiceImpl<AchievementMapper, Achievement> implements AchievementService {
    @Override
    public List<Achievement> getByUserId(Long userId) {
        return baseMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Achievement>().eq("user_id", userId));
    }

    @Override
    public List<Achievement> getByType(Long userId, String achievementType) {
        return baseMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Achievement>()
                .eq("user_id", userId)
                .eq("achievement_type", achievementType));
    }

    @Override
    public boolean addAchievement(Long userId, String achievementName, String description, String achievementType) {
        Achievement achievement = new Achievement();
        achievement.setUserId(userId);
        achievement.setAchievementName(achievementName);
        achievement.setDescription(description);
        achievement.setAchievementType(achievementType);
        achievement.setObtainTime(new Date());
        achievement.setCreateTime(new Date());
        achievement.setUpdateTime(new Date());
        return save(achievement);
    }

    // 保持兼容性的旧方法
    public boolean addAchievement(Long userId, String achievementName, String description) {
        return addAchievement(userId, achievementName, description, "learning"); // 默认学习类型
    }
}
