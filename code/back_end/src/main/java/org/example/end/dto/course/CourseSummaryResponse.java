package org.example.end.dto.course;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
// 课程概要响应
public class CourseSummaryResponse {
    private final Long id;                              // 课程ID
    private final String title;                         // 课程标题
    private final String category;                      // 课程分类
    private final BigDecimal price;                     // 课程价格
    private final String teacherName;                   // 教师名称
}


