package org.example.end.config;

import org.example.end.domain.User;
import org.example.end.domain.enums.Role;
import org.example.end.domain.enums.UserStatus;
import org.example.end.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;

// 数据初始化
@Configuration
public class DataInitializer {
    // 初始化管理员
    @Bean
    @ConditionalOnProperty(prefix = "app.seed", name = "admin", havingValue = "true")
    public CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // 如果管理员已存在，则不进行初始化
            String email = "admin@end.com";
            if (userRepository.existsByEmail(email)) {
                return;
            }

            // 初始化管理员
            userRepository.save(User.builder()
                    .name("Administrator")
                    .email(email)
                    .passwordHash(passwordEncoder.encode("admin1234"))
                    .role(Role.ADMIN)
                    .status(UserStatus.ACTIVE)
                    .build());
        };
    }
}


