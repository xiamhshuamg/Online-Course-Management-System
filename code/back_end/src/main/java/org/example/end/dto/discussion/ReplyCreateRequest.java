package org.example.end.dto.discussion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 回复创建请求
public class ReplyCreateRequest {
    @NotBlank
    @Size(max = 10000)
    private String content;
}


