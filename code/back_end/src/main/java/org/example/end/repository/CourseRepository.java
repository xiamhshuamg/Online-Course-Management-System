package org.example.end.repository;

import java.util.Collection;
import java.util.List;
import org.example.end.domain.Course;
import org.example.end.domain.enums.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

// 课程仓库
public interface CourseRepository extends JpaRepository<Course, Long> {
    // 根据教师ID排序
    List<Course> findByTeacherIdOrderByUpdatedAtDesc(Long teacherId);
    // 根据状态排序
    List<Course> findByStatusInOrderByUpdatedAtDesc(Collection<CourseStatus> statuses);

    @Query("""
            select c from Course c
            where c.status in :statuses
              and (:category is null or c.category = :category)
              and (:q is null or lower(c.title) like lower(concat('%', :q, '%')))
            order by c.updatedAt desc
            """)
    // 搜索已发布课程
    List<Course> searchPublished(
            @Param("statuses") Collection<CourseStatus> statuses,
            @Param("category") String category,
            @Param("q") String q
    );

    // 根据状态计数
    long countByStatus(CourseStatus status);

    // 新增：调用存储过程 sp_cascade_soft_delete_course
    // 来源：03_optimization.sql
    @Procedure(procedureName = "sp_cascade_soft_delete_course")
    void softDeleteCourse(@Param("p_course_id") Long courseId);
}


