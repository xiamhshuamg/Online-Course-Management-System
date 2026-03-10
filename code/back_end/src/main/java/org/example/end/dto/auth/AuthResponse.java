package org.example.end.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
// 认证响应
public class AuthResponse {
    private final String token;              // 令牌
    private final String role;               // 角色
    private final Long userId;               // 用户ID
    private final String name;               // 用户名
    private final String email;              // 邮箱
}


