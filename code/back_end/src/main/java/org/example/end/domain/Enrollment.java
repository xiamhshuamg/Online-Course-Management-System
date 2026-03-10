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
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.end.domain.enums.EnrollmentStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "enrollments",
        indexes = {
                @Index(name = "idx_enrollments_course", columnList = "course_id"),
                @Index(name = "idx_enrollments_student", columnList = "student_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_enrollment_course_student", columnNames = {"course_id", "student_id"})
        }
)
// 报名
public class Enrollment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EnrollmentStatus status;
    // 支付金额
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal paidAmount;
}


