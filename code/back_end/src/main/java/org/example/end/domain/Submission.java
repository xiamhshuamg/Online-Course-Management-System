package org.example.end.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.end.domain.enums.SubmissionStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "submissions",
        indexes = {
                @Index(name = "idx_submissions_assignment", columnList = "assignment_id"),
                @Index(name = "idx_submissions_student", columnList = "student_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_submission_assignment_student",
                        columnNames = {"assignment_id", "student_id"}
                )
        }
)
// 提交
public class Submission extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
//    // 内容
//    @Column(nullable = false, columnDefinition = "TEXT")
//    private String contentJson;
    // 提交时间
    @Column(nullable = false)
    private Instant submittedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubmissionStatus status;

    private Integer score;

    @Column(columnDefinition = "TEXT")
    private String feedback;
    // 评分时间
    private Instant gradedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graded_by")
    // 评分人
    private User gradedBy;
    // [关键修复]
    // 1. name = "content_json": 强制指定对应数据库里的 content_json 列
    // 2. columnDefinition = "TEXT": 确保能存下长文本，防止因长度限制被截断
    @Column(name = "content_json", columnDefinition = "TEXT")
    private String contentJson;
}


