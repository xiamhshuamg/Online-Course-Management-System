package org.example.end.dto.admin;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 决策请求
public class DecisionRequest {
    // 是否批准
    @NotNull
    private Boolean approved;
    // 备注
    @Size(max = 2000)
    private String remark;
}


