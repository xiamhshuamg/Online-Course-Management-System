package org.example.end.dto.discussion;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
// 回复响应
public class ReplyResponse {
    private final Long id;
    private final Long postId;
    private final Long authorId;
    private final String authorName;
    private final String content;
    private final Instant createdAt;
}


