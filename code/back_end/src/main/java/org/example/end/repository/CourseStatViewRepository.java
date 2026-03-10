package org.example.end.repository;

import org.example.end.domain.audit.SubmissionGradeAudit;
import org.example.end.domain.views.CourseStatView;
import org.example.end.domain.views.GradingDashboardView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


// 1. 课程统计视图 Repository
@Repository
public interface CourseStatViewRepository extends JpaRepository<CourseStatView, Long> {
    // 按总收入降序排列
    List<CourseStatView> findAllByOrderByTotalRevenueDesc();
}

