package org.example.end.dto.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
// 资源响应
public class ResourceResponse {
    private final Long id;
    private final String type;
    private final String name;
    private final String url;
}


