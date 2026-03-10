package org.example.end.domain.views;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

/**
 * 对应数据库视图: v_course_stats
 * 来源: 03_optimization.sql
 * 用途: 课程运营数据概览（高性能查询，避免复杂的 JOIN）
 */
@Entity
@Getter
@Immutable // 标记为不可变，防止 JPA 尝试更新视图
@Table(name = "v_course_stats")
public class CourseStatView {

    @Id // 视图必须有一个逻辑主键，这里用 course_id
    private Long courseId;

    private String courseTitle;
    private String courseStatus;
    private String teacherName;
    private Long enrolledCount;
    private BigDecimal totalRevenue;
}