package org.example.end.repository;

import java.util.List;
import java.util.Optional;
import org.example.end.domain.TeacherProfile;
import org.example.end.domain.enums.TeacherVerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

// 教师资料仓库
public interface TeacherProfileRepository extends JpaRepository<TeacherProfile, Long> {
    // 根据用户ID查询
    Optional<TeacherProfile> findByUserId(Long userId);

    // 根据验证状态计数
    long countByVerificationStatus(TeacherVerificationStatus status);

    // [王某新增] 根据验证状态查询列表，用于管理员审核
    List<TeacherProfile> findByVerificationStatusOrderByUpdatedAtDesc(TeacherVerificationStatus status);
}