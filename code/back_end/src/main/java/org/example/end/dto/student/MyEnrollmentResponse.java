package org.example.end.dto.student;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
// 我的报名响应
public class MyEnrollmentResponse {
    private final Long enrollmentId;                  // 报名ID
    private final Long courseId;                      // 课程ID
    private final String courseTitle;                 // 课程标题
    private final String status;                      // 状态
    private final BigDecimal paidAmount;              // 支付金额
}


