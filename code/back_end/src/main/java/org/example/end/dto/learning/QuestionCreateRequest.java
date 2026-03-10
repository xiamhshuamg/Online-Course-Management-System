package org.example.end.dto.learning;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.example.end.domain.enums.QuestionType;

@Getter
@Setter
// 问题创建请求
public class QuestionCreateRequest {
    @NotNull
    private QuestionType type;

    @NotBlank
    @Size(max = 5000)
    private String prompt;                        // 提示

    @Size(max = 20000)
    private String optionsJson;                   // 选项

    @Size(max = 20000)
    private String answerKeyJson;                 // 答案

    @NotNull
    private Integer score;                         // 分数
}


