package com.edu.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.platform.mapper.CollectionMapper;
import com.edu.platform.model.Collection;
import com.edu.platform.service.CollectionService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CollectionServiceImpl extends ServiceImpl<CollectionMapper, Collection> implements CollectionService {
    @Override
    public List<Collection> getByUserId(Long userId) {
        return baseMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Collection>().eq("user_id", userId));
    }

    @Override
    public boolean addCollection(Long userId, Long courseId) {
        if (isCollected(userId, courseId)) {
            return false;
        }
        Collection collection = new Collection();
        collection.setUserId(userId);
        collection.setCourseId(courseId);
        collection.setCreateTime(new Date());
        return save(collection);
    }

    @Override
    public boolean removeCollection(Long userId, Long courseId) {
        return remove(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Collection>()
                .eq("user_id", userId)
                .eq("course_id", courseId));
    }

    @Override
    public boolean isCollected(Long userId, Long courseId) {
        return count(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Collection>()
                .eq("user_id", userId)
                .eq("course_id", courseId)) > 0;
    }
}
