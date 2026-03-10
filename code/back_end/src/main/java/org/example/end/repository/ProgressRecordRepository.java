package org.example.end.repository;

import java.util.List;
import java.util.Optional;
import org.example.end.domain.ProgressRecord;
import org.springframework.data.jpa.repository.JpaRepository;

// 进度记录仓库
public interface ProgressRecordRepository extends JpaRepository<ProgressRecord, Long> {
    // 根据报名ID查询
    List<ProgressRecord> findByEnrollmentId(Long enrollmentId);
    // 根据报名ID和章节ID查询
    Optional<ProgressRecord> findByEnrollmentIdAndChapterId(Long enrollmentId, Long chapterId);
}


