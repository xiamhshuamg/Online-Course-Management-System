package org.example.end.dto.learning;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.example.end.domain.enums.AssignmentType;

@Getter
@Setter
// 作业创建请求
public class AssignmentCreateRequest {
    @NotNull
    private AssignmentType type;

    @NotBlank
    @Size(max = 160)
    private String title;

    @NotBlank
    @Size(max = 5000)
    private String description;
    // 截止日期
    private Instant dueAt;
    // 最高分数
    @NotNull
    private Integer maxScore;
}


