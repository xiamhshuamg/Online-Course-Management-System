package org.example.end.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 章节创建请求
public class ChapterCreateRequest {
    @NotBlank
    @Size(max = 120)
    private String title;

    // 排序顺序
    @NotNull
    private Integer sortOrder;
}


