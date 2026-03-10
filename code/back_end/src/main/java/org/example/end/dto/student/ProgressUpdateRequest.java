package org.example.end.dto.student;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 进度更新请求
public class ProgressUpdateRequest {
    @NotNull
    private Long courseId;

    @NotNull
    private Long chapterId;

    private boolean completed;

    @Min(0)
    private int watchedSeconds;                    // 观看秒数
}


