package org.example.end.repository;

import java.util.List;
import org.example.end.domain.DiscussionPost;
import org.springframework.data.jpa.repository.JpaRepository;

// 讨论帖子仓库
public interface DiscussionPostRepository extends JpaRepository<DiscussionPost, Long> {
    // 根据课程ID和删除状态排序
    List<DiscussionPost> findByCourseIdAndDeletedFalseOrderByPinnedDescUpdatedAtDesc(Long courseId);
}


