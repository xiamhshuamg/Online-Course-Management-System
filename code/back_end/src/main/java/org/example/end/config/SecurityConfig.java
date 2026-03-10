package org.example.end.config;

import org.example.end.security.DbUserDetailsService;
import org.example.end.security.JwtAuthenticationFilter;
import org.example.end.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// 安全配置
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 密码编码器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // JWT 认证过滤器
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, DbUserDetailsService dbUserDetailsService) {
        return new JwtAuthenticationFilter(jwtService, dbUserDetailsService);
    }

    // 安全过滤器链
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) 
    throws Exception {
        // 禁用 CSRF
        http.csrf(csrf -> csrf.disable());
        // 设置会话创建策略为无状态
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // 授权请求
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/api-ui.html").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/courses/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/teacher/**").hasRole("TEACHER")
                .requestMatchers("/api/student/**").hasRole("STUDENT")
                .anyRequest().authenticated()
        );
        // 启用 HTTP 基本认证
        http.httpBasic(Customizer.withDefaults());
        // 添加 JWT 过滤器
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        // 构建安全过滤器链
        return http.build();
    }
}


