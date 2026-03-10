package org.example.end.web;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

import org.example.end.domain.SystemSetting;
import org.example.end.dto.admin.DecisionRequest;
import org.example.end.dto.admin.OverviewStatsResponse;
import org.example.end.dto.admin.TeacherProfileResponse;
import org.example.end.dto.admin.UserResponse;
import org.example.end.dto.course.CourseDetailResponse;
import org.example.end.service.AdminService;
import org.example.end.service.CourseService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
// 管理员控制器       
public class AdminController {

    private final AdminService adminService;                             // 管理员服务
    private final CourseService courseService;                           // 课程服务

    public AdminController(AdminService adminService, CourseService courseService) {
        this.adminService = adminService;
        this.courseService = courseService;
    }

    @PostMapping("/teachers/{userId}/decision")
    // 决定教师
    public TeacherProfileResponse decideTeacher(@PathVariable Long userId, @Valid @RequestBody DecisionRequest req) {
        return adminService.decideTeacher(userId, req.getApproved(), req.getRemark());
    }

    @PostMapping("/courses/{courseId}/decision")
    // 决定课程
    public CourseDetailResponse decideCourse(@PathVariable Long courseId, @Valid @RequestBody DecisionRequest req) {
        return courseService.adminDecideCourse(courseId, req.getApproved(), req.getRemark());
    }

    @GetMapping("/users")
    // 列出用户
    public List<UserResponse> users() {
        return adminService.listUsers();
    }

    @PostMapping("/users/{userId}/freeze")
    // 冻结用户
    public UserResponse freeze(@PathVariable Long userId) {
        return adminService.freezeUser(userId, true);
    }

    @PostMapping("/users/{userId}/unfreeze")
    // 解冻用户
    public UserResponse unfreeze(@PathVariable Long userId) {
        return adminService.freezeUser(userId, false);
    }

    @GetMapping("/stats/overview")
    // 概览统计
    public OverviewStatsResponse overview() {
        return adminService.overview();
    }

    //王某人添加待审核课程
    @GetMapping("/courses/pending")
    public List<org.example.end.dto.course.CourseDetailResponse> getPendingCourses() {
        return adminService.listPendingCourses();
    }
    // [王某新增] 待审核教师接口，解决前端 404 问题
    @GetMapping("/teachers/pending")
    public List<org.example.end.dto.admin.TeacherApplicationResponse> getPendingTeachers() {
        return adminService.listPendingTeachers();
    }

    // [修复] 获取系统设置
    @GetMapping("/settings")
    public SystemSetting getSettings() {
        return adminService.getSettings();
    }

    // [修复] 更新系统设置
    @PutMapping("/settings")
    public SystemSetting updateSettings(@RequestBody Map<String, Object> body) {
        // 前端传来的可能是 { data: { siteName: ... } } 或者直接 { siteName: ... }
        // 为了兼容前端 http.js 的封装，如果包含 data key，取 data，否则取 body
        Map<String, Object> data = body;
        if (body.containsKey("data") && body.get("data") instanceof Map) {
            data = (Map<String, Object>) body.get("data");
        }
        return adminService.updateSettings(data);
    }
}


