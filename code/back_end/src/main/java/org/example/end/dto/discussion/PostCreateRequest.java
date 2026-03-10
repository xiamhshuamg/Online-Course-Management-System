package org.example.end.dto.discussion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 帖子创建请求
public class PostCreateRequest {
    @NotBlank
    @Size(max = 160)
    private String title;                      // 标题

    @NotBlank
    @Size(max = 10000)
    private String content;                    // 内容
}


