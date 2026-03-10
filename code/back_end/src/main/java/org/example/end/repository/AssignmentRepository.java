package org.example.end.repository;

import java.util.Collection;
import java.util.List;
import org.example.end.domain.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

// 作业仓库
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    // 根据课程ID排序
    List<Assignment> findByCourseIdOrderByUpdatedAtDesc(Long courseId);
    // 王某 (添加统计某课程作业数的方法)
    long countByCourseId(Long courseId);

    // [新增] 查询多个课程的所有作业，用于学生“我的作业”列表
    List<Assignment> findByCourseIdInOrderByDueAtAsc(Collection<Long> courseIds);

    // 新增：调用存储过程 sp_batch_grade_assignment
    // 来源：03_optimization.sql
    @Procedure(procedureName = "sp_batch_grade_assignment")
    void batchGradeAssignment(
            @Param("p_assignment_id") Long assignmentId,
            @Param("p_score") Integer score,
            @Param("p_admin_id") Long adminId
    );
}