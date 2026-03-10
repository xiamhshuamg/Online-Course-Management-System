package org.example.end.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
// 用户响应
public class UserResponse {
    private final Long id;                  // 用户ID
    private final String name;              // 用户名
    private final String email;             // 邮箱
    private final String role;              // 角色
    private final String status;            // 状态
}


