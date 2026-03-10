package org.example.end.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许前端地址，开发环境通常是 localhost:5173
        config.setAllowedOriginPatterns(List.of("*")); 
        // 允许的头信息
        config.addAllowedHeader("*");
        // 允许的方法 GET, POST, PUT, DELETE 等
        config.addAllowedMethod("*");
        // 允许携带凭证（如 Cookie 或 Authorization 头）
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}