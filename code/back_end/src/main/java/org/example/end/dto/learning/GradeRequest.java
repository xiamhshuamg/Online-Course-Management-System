package org.example.end.dto.learning;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 评分请求
public class GradeRequest {
    @NotNull
    private Integer score;

    @Size(max = 5000)
    private String feedback;
}


