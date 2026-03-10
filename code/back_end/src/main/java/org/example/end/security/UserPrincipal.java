package org.example.end.security;

import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.example.end.domain.User;
import org.example.end.domain.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
// 用户主体
public class UserPrincipal implements UserDetails {

    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    @Override
    // 获取权限
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    // 获取密码
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    // 获取用户名
    public String getUsername() {
        return String.valueOf(user.getId());
    }

    @Override
    // 账户是否过期
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    // 账户是否锁定
    public boolean isAccountNonLocked() {
        return user.getStatus() != UserStatus.FROZEN;
    }

    @Override
    // 凭证是否过期
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    // 账户是否启用
    public boolean isEnabled() {
        return user.getStatus() == UserStatus.ACTIVE;
    }
}


