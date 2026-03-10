package org.example.end.dto.learning;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
// 作业响应
public class AssignmentResponse {
    private final Long id;                              // 作业ID
    private final Long courseId;                        // 课程ID
    private final String type;                          // 作业类型
    private final String title;                         // 作业标题
    private final String description;                   // 作业描述
    private final Instant dueAt;                       // 截止日期
    private final Integer maxScore;                     // 最高分数
}


