package org.example.end.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
// 教师资料响应
public class TeacherProfileResponse {
    private final Long userId;              // 用户ID
    private final String verificationStatus; // 认证状态
    private final String adminRemark;        // 管理员备注
}


