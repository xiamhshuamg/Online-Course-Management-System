package org.example.end.dto.discussion;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
// 帖子响应
public class PostResponse {
    private final Long id;                              // 帖子ID
    private final Long courseId;                        // 课程ID
    private final Long authorId;                        // 作者ID
    private final String authorName;                    // 作者名称
    private final String title;                         // 标题
    private final String content;                       // 内容
    private final boolean pinned;                       // 是否置顶
    private final Instant createdAt;                   // 创建时间
    // [新增] 回复数量
    private final long replyCount;
}