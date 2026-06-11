package com.edu.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.platform.model.LearningProgress;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LearningProgressMapper extends BaseMapper<LearningProgress> {
}