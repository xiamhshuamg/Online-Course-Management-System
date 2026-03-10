package org.example.end.dto.course;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
// 课程详情响应
public class CourseDetailResponse {
    private final Long id;                              // 课程ID
    private final String title;                         // 课程标题
    private final String description;                   // 课程描述
    private final BigDecimal price;                     // 课程价格
    private final String category;                      // 课程分类
    private final String status;                        // 课程状态
    private final Long teacherId;                       // 教师ID
    private final String teacherName;                   // 教师名称
    private final List<ChapterResponse> chapters;       // 章节

    // 王某 (添加统计字段，解决教师端统计数据显示为0的问题)
    private final Long studentCount;
    private final Long assignmentCount;
}


