package org.example.end.dto.learning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
// 问题响应
public class QuestionResponse {
    private final Long id;                              // 问题ID
    private final Long assignmentId;                    // 作业ID
    private final String type;                          // 问题类型
    private final String prompt;                        // 提示
    private final String optionsJson;                   // 选项
    private final Integer score;                         // 分数
}


