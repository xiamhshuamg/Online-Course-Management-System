package org.example.end.domain.views;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.time.Instant;

/**
 * 对应数据库视图: v_grading_dashboard
 * 来源: 03_optimization.sql
 * 用途: 待评分作业看板
 */
@Entity
@Getter
@Immutable
@Table(name = "v_grading_dashboard")
public class GradingDashboardView {

    @Id // 这里 submission_id 是唯一的
    private Long submissionId;

    private String courseTitle;
    private String assignmentTitle;
    private String studentName;
    private Instant submittedAt;
    private String status;
}