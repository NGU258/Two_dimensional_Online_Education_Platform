package com.edu.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.platform.model.Collection;

import java.util.List;

public interface CollectionService extends IService<Collection> {
    List<Collection> getByUserId(Long userId);
    boolean addCollection(Long userId, Long courseId);
    boolean removeCollection(Long userId, Long courseId);
    boolean isCollected(Long userId, Long courseId);
}
