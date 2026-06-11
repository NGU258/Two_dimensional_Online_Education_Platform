package com.edu.platform.controller;

import com.edu.platform.model.Course;
import com.edu.platform.service.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/course")
@Api(tags = "课程管理", description = "课程的创建、更新、删除、查询等操作")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping("/create")
    @ApiOperation(value = "创建课程", notes = "创建新的课程，需要提供课程信息")
    public Map<String, Object> create(@ApiParam(name = "course", value = "课程信息", required = true) @RequestBody Course course) {
        Map<String, Object> result = new HashMap<>();
        try {
            Course createdCourse = courseService.create(course);
            result.put("code", 200);
            result.put("message", "创建课程成功");
            result.put("data", createdCourse);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "创建课程失败: " + e.getMessage());
        }
        return result;
    }

    @PutMapping("/update")
    @ApiOperation(value = "更新课程", notes = "更新课程信息，需要提供课程ID和更新的信息")
    public Map<String, Object> update(@ApiParam(name = "course", value = "课程信息", required = true) @RequestBody Course course) {
        Map<String, Object> result = new HashMap<>();
        try {
            Course updatedCourse = courseService.update(course);
            result.put("code", 200);
            result.put("message", "更新课程成功");
            result.put("data", updatedCourse);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "更新课程失败: " + e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除课程", notes = "根据课程ID删除课程")
    public Map<String, Object> delete(@ApiParam(name = "id", value = "课程ID", required = true) @PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            courseService.delete(id);
            result.put("code", 200);
            result.put("message", "删除课程成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除课程失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/get/{id}")
    @ApiOperation(value = "获取课程详情", notes = "根据课程ID获取课程详细信息")
    public Map<String, Object> getById(@ApiParam(name = "id", value = "课程ID", required = true) @PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Course course = courseService.getById(id);
            if (course != null) {
                result.put("code", 200);
                result.put("message", "获取课程成功");
                result.put("data", course);
            } else {
                result.put("code", 404);
                result.put("message", "课程不存在");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取课程失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/list")
    @ApiOperation(value = "获取课程列表", notes = "获取所有课程的列表")
    public Map<String, Object> list() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Course> courses = courseService.list();
            result.put("code", 200);
            result.put("message", "获取课程列表成功");
            result.put("data", courses);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取课程列表失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/listByCategory/{categoryId}")
    @ApiOperation(value = "按分类获取课程", notes = "根据分类ID获取课程列表")
    public Map<String, Object> listByCategoryId(@ApiParam(name = "categoryId", value = "分类ID", required = true) @PathVariable Integer categoryId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Course> courses = courseService.listByCategoryId(categoryId);
            result.put("code", 200);
            result.put("message", "获取分类课程列表成功");
            result.put("data", courses);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取分类课程列表失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/listByTeacher/{teacherId}")
    @ApiOperation(value = "按教师获取课程", notes = "根据教师ID获取课程列表")
    public Map<String, Object> listByTeacherId(@ApiParam(name = "teacherId", value = "教师ID", required = true) @PathVariable Integer teacherId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Course> courses = courseService.listByTeacherId(teacherId);
            result.put("code", 200);
            result.put("message", "获取教师课程列表成功");
            result.put("data", courses);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取教师课程列表失败: " + e.getMessage());
        }
        return result;
    }
}