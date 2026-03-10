package org.example.end.security;

import org.example.end.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

// 安全工具类
public final class SecurityUtils {

    private SecurityUtils() {
    }
    // 获取当前用户
    public static UserPrincipal requirePrincipal() {
        // 获取认证信息
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // 如果认证信息为空，则抛出异常
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal up)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        // 返回当前用户
        return up;
    }
    // 获取当前用户ID
    public static Long currentUserId() {
        return requirePrincipal().getUser().getId();
    }
}


