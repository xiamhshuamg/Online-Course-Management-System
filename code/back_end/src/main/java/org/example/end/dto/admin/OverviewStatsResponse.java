package org.example.end.dto.admin;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
// 概览统计响应
public class OverviewStatsResponse {
    private final long students;             // 学生数量
    private final long teachers;             // 教师数量
    private final long admins;               // 管理员数量
    private final long teacherPending;       // 教师待审核数量
    private final long coursePending;        // 课程待审核数量
    private final long enrollments;          // 报名数量
    private final BigDecimal revenue;        // 收入
}


