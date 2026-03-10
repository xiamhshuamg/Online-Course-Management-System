package org.example.end.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

// JWT认证过滤器
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;                      // JWT服务
    private final DbUserDetailsService userDetailsService;    // 用户详情服务

    public JwtAuthenticationFilter(JwtService jwtService, DbUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        // 如果令牌为空，则继续过滤
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        // 如果认证信息不为空，则继续过滤
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 获取令牌
        String token = header.substring("Bearer ".length()).trim();
        // 解析令牌
        try {
            Jws<Claims> jws = jwtService.parse(token);
            String userId = jws.getPayload().getSubject();

            UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception ignored) {
            // invalid token -> treat as anonymous
        }
        // 继续过滤
        filterChain.doFilter(request, response);
    }
}


