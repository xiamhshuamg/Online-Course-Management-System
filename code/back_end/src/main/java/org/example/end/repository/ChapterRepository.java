package org.example.end.repository;

import java.util.List;
import org.example.end.domain.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
// 章节仓库
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    // 根据课程ID排序
    List<Chapter> findByCourseIdOrderBySortOrderAsc(Long courseId);
}


