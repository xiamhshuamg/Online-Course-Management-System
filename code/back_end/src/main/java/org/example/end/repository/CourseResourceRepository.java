package org.example.end.repository;

import java.util.List;
import org.example.end.domain.CourseResource;
import org.springframework.data.jpa.repository.JpaRepository;

// 课程资源仓库
public interface CourseResourceRepository extends JpaRepository<CourseResource, Long> {
    // 根据章节ID排序
    List<CourseResource> findByChapterIdOrderByUpdatedAtDesc(Long chapterId);
    // 根据课程ID查询
    List<CourseResource> findByChapterCourseId(Long courseId);
}


