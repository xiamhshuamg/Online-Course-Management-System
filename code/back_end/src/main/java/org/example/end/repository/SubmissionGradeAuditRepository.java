package org.example.end.repository;

import org.example.end.domain.audit.SubmissionGradeAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// 3. 审计日志 Repository
@Repository
public interface SubmissionGradeAuditRepository extends JpaRepository<SubmissionGradeAudit, Long> {
    // 查询某次提交的所有修改记录，按时间倒序
    List<SubmissionGradeAudit> findBySubmissionIdOrderByChangedAtDesc(Long submissionId);
}