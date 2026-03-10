package org.example.end.repository;

import java.util.List;
import org.example.end.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

// 问题仓库
public interface QuestionRepository extends JpaRepository<Question, Long> {
    // 根据作业ID排序
    List<Question> findByAssignmentIdOrderByIdAsc(Long assignmentId);
}


