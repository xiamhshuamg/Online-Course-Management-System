package org.example.end.service;

import org.example.end.domain.audit.SubmissionGradeAudit;
import org.example.end.domain.views.CourseStatView;
import org.example.end.domain.views.GradingDashboardView;
import org.example.end.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OptimizationService {

    private final CourseStatViewRepository courseStatViewRepository;
    private final GradingDashboardViewRepository gradingDashboardViewRepository;
    private final SubmissionGradeAuditRepository auditRepository;
    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;

    public OptimizationService(
            CourseStatViewRepository courseStatViewRepository,
            GradingDashboardViewRepository gradingDashboardViewRepository,
            SubmissionGradeAuditRepository auditRepository,
            AssignmentRepository assignmentRepository,
            CourseRepository courseRepository) {
        this.courseStatViewRepository = courseStatViewRepository;
        this.gradingDashboardViewRepository = gradingDashboardViewRepository;
        this.auditRepository = auditRepository;
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * 获取课程高性能统计 (基于视图 v_course_stats)
     */
    public List<CourseStatView> getCourseStats() {
        return courseStatViewRepository.findAllByOrderByTotalRevenueDesc();
    }

    /**
     * 获取待评分看板 (基于视图 v_grading_dashboard)
     */
    public List<GradingDashboardView> getGradingDashboard() {
        return gradingDashboardViewRepository.findAll();
    }

    /**
     * 执行批量评分 (基于存储过程 sp_batch_grade_assignment)
     */
    @Transactional
    public void batchGrade(Long assignmentId, Integer score, Long operatorId) {
        // 直接调用 Repository 中定义的存储过程接口
        assignmentRepository.batchGradeAssignment(assignmentId, score, operatorId);
    }

    /**
     * 执行课程级联软删除 (基于存储过程 sp_cascade_soft_delete_course)
     */
    @Transactional
    public void deleteCourseCascaded(Long courseId) {
        // 直接调用 Repository 中定义的存储过程接口
        courseRepository.softDeleteCourse(courseId);
    }
    
    /**
     * 获取分数修改审计日志 (基于表 submission_grade_audit)
     */
    public List<SubmissionGradeAudit> getGradeHistory(Long submissionId) {
        return auditRepository.findBySubmissionIdOrderByChangedAtDesc(submissionId);
    }
}