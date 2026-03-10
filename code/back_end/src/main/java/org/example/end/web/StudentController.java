package org.example.end.web;

import jakarta.validation.Valid;
import java.util.List;
import org.example.end.dto.course.CourseDetailResponse;
import org.example.end.dto.student.EnrollResponse;
import org.example.end.dto.student.LearningProgressResponse;
import org.example.end.dto.student.MyEnrollmentResponse;
import org.example.end.dto.student.ProgressUpdateRequest;
import org.example.end.security.SecurityUtils;
import org.example.end.service.StudentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student")
// 学生基础控制器：负责报名、课程列表、进度
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/courses/{courseId}/enroll")
    // 报名课程
    public EnrollResponse enroll(@PathVariable Long courseId) {
        return studentService.enroll(SecurityUtils.currentUserId(), courseId);
    }

    @GetMapping("/enrollments")
    // 我的报名
    public List<MyEnrollmentResponse> myEnrollments() {
        return studentService.myEnrollments(SecurityUtils.currentUserId());
    }

    @GetMapping("/courses/{courseId}/content")
    // 课程内容
    public CourseDetailResponse content(@PathVariable Long courseId) {
        return studentService.courseContent(SecurityUtils.currentUserId(), courseId);
    }

    @PostMapping("/progress")
    // 更新进度
    public void updateProgress(@Valid @RequestBody ProgressUpdateRequest req) {
        studentService.updateProgress(SecurityUtils.currentUserId(), req);
    }

    // [王某新增] 获取学习进度接口
    @GetMapping("/progress")
    public List<LearningProgressResponse> getProgress() {
        return studentService.getLearningProgress(SecurityUtils.currentUserId());
    }
}