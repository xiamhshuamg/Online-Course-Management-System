package org.example.end.domain.audit;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * 对应数据库表: submission_grade_audit
 * 来源: 04_optimization_pro.sql
 * 用途: 记录分数修改历史（由数据库触发器自动插入，这里只读或查询）
 */
@Entity
@Getter
@Setter
@Table(name = "submission_grade_audit")
public class SubmissionGradeAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long submissionId;

    private Integer oldScore;
    private Integer newScore;

    private Long changedByUserId;

    private Instant changedAt;
}