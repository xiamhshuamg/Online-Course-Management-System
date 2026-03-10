package org.example.end.dto.discussion;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 置顶请求
public class PinRequest {
    @NotNull
    private Boolean pinned;                    // 是否置顶
}

