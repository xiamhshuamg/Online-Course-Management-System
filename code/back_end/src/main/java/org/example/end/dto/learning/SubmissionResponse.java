package org.example.end.dto.learning;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
// 提交响应
public class SubmissionResponse {
    private final Long id;                              // 提交ID
    private final Long assignmentId;                    // 作业ID
    private final String assignmentTitle;               // [新增] 作业标题
    private final Long studentId;                       // 学生ID
    private final String status;                        // 状态
    private final Integer score;                        // 分数
    private final String feedback;                      // 反馈
    private final Instant submittedAt;                  // 提交时间
    private final Instant gradedAt;                     // 评分时间
    private final String studentName;                   // 学生姓名
    // [修复] 添加内容字段，否则前端拿不到学生提交的作业内容
    private final String contentJson;
}