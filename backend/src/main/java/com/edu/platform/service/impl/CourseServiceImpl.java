package com.edu.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.edu.platform.mapper.CourseMapper;
import com.edu.platform.model.Course;
import com.edu.platform.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public Course create(Course course) {
        course.setStatus(1); // 默认启用
        course.setViewCount(0);
        course.setLikeCount(0);
        course.setCommentCount(0);
        course.setCreateTime(LocalDateTime.now());
        course.setUpdateTime(LocalDateTime.now());
        courseMapper.insert(course);
        return course;
    }

    @Override
    public Course update(Course course) {
        course.setUpdateTime(LocalDateTime.now());
        courseMapper.updateById(course);
        return course;
    }

    @Override
    public void delete(Long id) {
        courseMapper.deleteById(id);
    }

    @Override
    public Course getById(Long id) {
        return courseMapper.selectById(id);
    }

    @Override
    public List<Course> list() {
        return courseMapper.selectList(null);
    }

    @Override
    public List<Course> listByCategoryId(Integer categoryId) {
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.eq("category_id", categoryId);
        return courseMapper.selectList(wrapper);
    }

    @Override
    public List<Course> listByTeacherId(Integer teacherId) {
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.eq("teacher_id", teacherId);
        return courseMapper.selectList(wrapper);
    }
}