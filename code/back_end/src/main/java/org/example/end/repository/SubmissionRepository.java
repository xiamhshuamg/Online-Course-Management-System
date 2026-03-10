package org.example.end.repository;

import java.util.List;
import java.util.Optional;
import org.example.end.domain.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

// 提交仓库
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    // 根据作业ID和学生ID查询
    Optional<Submission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

    // 根据作业ID排序
    List<Submission> findByAssignmentIdOrderByUpdatedAtDesc(Long assignmentId);

    // [新增] 查询某教师(通过课程关联)的所有作业提交，按提交时间倒序
    // 路径: Submission -> Assignment -> Course -> Teacher -> Id
    List<Submission> findByAssignment_Course_Teacher_IdOrderBySubmittedAtDesc(Long teacherId);
}