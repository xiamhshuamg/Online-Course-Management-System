package org.example.end.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.example.end.domain.enums.ResourceType;

@Getter
@Setter
// 资源创建请求
public class ResourceCreateRequest {
    @NotNull
    private ResourceType type;

    @NotBlank
    @Size(max = 160)
    private String name;

    @NotBlank
    @Size(max = 2000)
    private String url;
}


