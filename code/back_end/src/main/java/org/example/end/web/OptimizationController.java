package org.example.end.web;

import org.example.end.domain.audit.SubmissionGradeAudit;
import org.example.end.domain.views.CourseStatView;
import org.example.end.domain.views.GradingDashboardView;
import org.example.end.security.SecurityUtils;
import org.example.end.service.OptimizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/opt")
public class OptimizationController {

    private final OptimizationService optimizationService;

    public OptimizationController(OptimizationService optimizationService) {
        this.optimizationService = optimizationService;
    }

    // 1. 获取课程统计数据
    @GetMapping("/stats/courses")
    public List<CourseStatView> getCourseStats() {
        return optimizationService.getCourseStats();
    }

    // 2. 获取待评分看板
    @GetMapping("/dashboard/grading")
    public List<GradingDashboardView> getGradingDashboard() {
        return optimizationService.getGradingDashboard();
    }

    // 3. 批量评分接口
    @PostMapping("/assignments/{id}/batch-grade")
    public ResponseEntity<?> batchGrade(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer score = body.get("score");
        if (score == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Score is required"));
        }
        
        Long operatorId = SecurityUtils.currentUserId();
        optimizationService.batchGrade(id, score, operatorId);
        
        return ResponseEntity.ok(Map.of("message", "Batch grading executed successfully"));
    }

    // 4. 级联软删除课程接口
    @DeleteMapping("/courses/{id}/cascade")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        optimizationService.deleteCourseCascaded(id);
        return ResponseEntity.ok(Map.of("message", "Course soft deleted successfully"));
    }
    
    // 5. 查看审计日志
    @GetMapping("/submissions/{id}/audit")
    public List<SubmissionGradeAudit> getAuditLog(@PathVariable Long id) {
        return optimizationService.getGradeHistory(id);
    }
}