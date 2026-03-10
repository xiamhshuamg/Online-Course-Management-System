package org.example.end.repository;

import org.example.end.domain.views.GradingDashboardView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradingDashboardViewRepository extends JpaRepository<GradingDashboardView, Long> {
    // 可以添加按课程名搜索的方法
    List<GradingDashboardView> findByCourseTitleContaining(String title);
}