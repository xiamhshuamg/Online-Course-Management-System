package org.example.end.repository;

import java.util.List;
import java.util.Optional;
import org.example.end.domain.Enrollment;
import org.example.end.domain.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
// 报名仓库
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    // 根据课程ID和学生ID查询
    Optional<Enrollment> findByCourseIdAndStudentId(Long courseId, Long studentId);
    // 根据学生ID和状态排序
    List<Enrollment> findByStudentIdAndStatusOrderByUpdatedAtDesc(Long studentId, EnrollmentStatus status);
    // 根据课程ID和状态排序
    List<Enrollment> findByCourseIdAndStatusOrderByUpdatedAtDesc(Long courseId, EnrollmentStatus status);

    @Query("select coalesce(sum(e.paidAmount), 0) from Enrollment e where e.course.id = :courseId and e.status = :status")
    // 根据课程ID和状态计算支付金额
    java.math.BigDecimal sumPaidAmountByCourseIdAndStatus(@Param("courseId") Long courseId, @Param("status") EnrollmentStatus status);

    @Query("select coalesce(sum(e.paidAmount), 0) from Enrollment e where e.status = :status")
    // 根据状态计算支付金额
    java.math.BigDecimal sumPaidAmountByStatus(@Param("status") EnrollmentStatus status);
    // 根据状态计数
    long countByStatus(EnrollmentStatus status);
    // 王某 (添加统计某课程学生数的方法)
    long countByCourseIdAndStatus(Long courseId, EnrollmentStatus status);
}


