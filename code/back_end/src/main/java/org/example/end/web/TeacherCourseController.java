package org.example.end.web;

import jakarta.validation.Valid;
import java.util.List;
import org.example.end.dto.course.ChapterCreateRequest;
import org.example.end.dto.course.ChapterResponse;
import org.example.end.dto.course.CourseCreateRequest;
import org.example.end.dto.course.CourseDetailResponse;
import org.example.end.dto.course.CourseUpdateRequest;
import org.example.end.dto.course.ResourceCreateRequest;
import org.example.end.dto.course.ResourceResponse;
import org.example.end.security.SecurityUtils;
import org.example.end.service.CourseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teacher")
// 教师课程控制器       
public class TeacherCourseController {

    private final CourseService courseService;                           // 课程服务

    public TeacherCourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/courses")
    // 列出教师课程
    public List<CourseDetailResponse> myCourses() {
        return courseService.listTeacherCourses(SecurityUtils.currentUserId());
    }

    @PostMapping("/courses")
    // 创建课程
    public CourseDetailResponse create(@Valid @RequestBody CourseCreateRequest req) {
        return courseService.createCourse(SecurityUtils.currentUserId(), req);
    }

    @PutMapping("/courses/{courseId}")
    // 更新课程
    public CourseDetailResponse update(@PathVariable Long courseId, @Valid @RequestBody CourseUpdateRequest req) {
        return courseService.updateCourse(SecurityUtils.currentUserId(), courseId, req);
    }

    @PostMapping("/courses/{courseId}/chapters")
    // 添加章节
    public ChapterResponse addChapter(@PathVariable Long courseId, @Valid @RequestBody ChapterCreateRequest req) {
        return courseService.addChapter(SecurityUtils.currentUserId(), courseId, req);
    }

    @PostMapping("/chapters/{chapterId}/resources")
    // 添加资源
    public ResourceResponse addResource(@PathVariable Long chapterId, @Valid @RequestBody ResourceCreateRequest req) {
        return courseService.addResource(SecurityUtils.currentUserId(), chapterId, req);
    }
}


