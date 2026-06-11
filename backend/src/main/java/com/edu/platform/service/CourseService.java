package com.edu.platform.service;

import com.edu.platform.model.Course;

import java.util.List;

public interface CourseService {
    Course create(Course course);
    Course update(Course course);
    void delete(Long id);
    Course getById(Long id);
    List<Course> list();
    List<Course> listByCategoryId(Integer categoryId);
    List<Course> listByTeacherId(Integer teacherId);
}