package org.example.end.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseCreateRequest {
    @NotBlank
    @Size(max = 120)
    private String title;

    @NotBlank
    @Size(min = 10, max = 5000)
    private String description;

    @NotNull
    @PositiveOrZero
    private BigDecimal price;

    @NotBlank
    @Size(max = 60)
    // 分类
    private String category;
}


