package org.example.end.repository;

import java.util.List;
import org.example.end.domain.DiscussionReply;
import org.springframework.data.jpa.repository.JpaRepository;

// 讨论回复仓库
public interface DiscussionReplyRepository extends JpaRepository<DiscussionReply, Long> {
    // 根据帖子ID和删除状态排序
    List<DiscussionReply> findByPostIdAndDeletedFalseOrderByUpdatedAtAsc(Long postId);

    // [新增] 统计回复数量 (不包含已删除的)
    long countByPostIdAndDeletedFalse(Long postId);
}