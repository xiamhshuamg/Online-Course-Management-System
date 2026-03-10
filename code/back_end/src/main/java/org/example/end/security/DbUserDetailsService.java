package org.example.end.security;

import org.example.end.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
// 数据库用户详情服务
public class DbUserDetailsService implements UserDetailsService {
    // 用户仓库
    private final UserRepository userRepository;

    public DbUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    // 根据用户名加载用户详情
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Long userId;
        try {
            userId = Long.valueOf(username);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid user id");
        }
        // 根据用户ID查询用户
        return userRepository.findById(userId)
                .map(UserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}


