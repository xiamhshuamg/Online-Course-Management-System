package org.example.end.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.end.domain.enums.TeacherVerificationStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "teacher_profiles")
// 教师资料
public class TeacherProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    // 资格证书
    @Column(nullable = false, columnDefinition = "TEXT")
    private String qualificationText;
    // 认证状态
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TeacherVerificationStatus verificationStatus;
    // 管理员备注
    @Column(columnDefinition = "TEXT")
    private String adminRemark;
}


