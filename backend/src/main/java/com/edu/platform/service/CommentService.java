package com.edu.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.platform.model.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {
    List<Comment> getByCourseId(Long courseId);
    List<Comment> getByUserId(Long userId);
    boolean addComment(Long userId, Long courseId, String content);
}
