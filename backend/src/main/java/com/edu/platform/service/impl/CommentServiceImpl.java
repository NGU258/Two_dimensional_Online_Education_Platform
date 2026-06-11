package com.edu.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.platform.mapper.CommentMapper;
import com.edu.platform.model.Comment;
import com.edu.platform.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Override
    public List<Comment> getByCourseId(Long courseId) {
        return baseMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Comment>().eq("course_id", courseId));
    }

    @Override
    public List<Comment> getByUserId(Long userId) {
        return baseMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Comment>().eq("user_id", userId));
    }

    @Override
    public boolean addComment(Long userId, Long courseId, String content) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setCourseId(courseId);
        comment.setContent(content);
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());
        return save(comment);
    }
}
