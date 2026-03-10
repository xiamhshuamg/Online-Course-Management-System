package org.example.end.dto.course;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
// 章节响应
public class ChapterResponse {
    private final Long id;                              // 章节ID
    private final String title;                         // 章节标题
    private final Integer sortOrder;                   // 排序顺序
    private final List<ResourceResponse> resources;     // 资源
}


