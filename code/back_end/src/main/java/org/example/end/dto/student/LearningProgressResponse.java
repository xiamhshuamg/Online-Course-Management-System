package org.example.end.dto.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
// [王某新增] 学习进度响应 DTO
public class LearningProgressResponse {
    private final Long courseId;
    private final String courseTitle;
    private final int chapterTotal;      // 总章节数
    private final int chapterCompleted;  // 已完成章节数
    private final int progress;          // 进度百分比 (0-100)
    private final Instant lastStudyTime; // 最近学习时间
}