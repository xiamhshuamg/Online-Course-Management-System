package org.example.end.web;

import java.util.List;
import org.example.end.dto.course.CourseDetailResponse;
import org.example.end.dto.course.CourseSummaryResponse;
import org.example.end.service.CourseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
// 公开课程控制器       
public class CoursePublicController {

    private final CourseService courseService;                           // 课程服务

    public CoursePublicController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    // 列出已发布课程
    public List<CourseSummaryResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category
    ) {
        return courseService.listPublished(q, category);
    }

    @GetMapping("/{courseId}")
    // 获取公开课程
    public CourseDetailResponse detail(@PathVariable Long courseId) {
        return courseService.getPublicCourse(courseId);
    }
}


