package org.example.end.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.example.end.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
// JWT服务
public class JwtService {

    private final String issuer;                   // 发行者
    private final String secret;                   // 密钥
    private final long expirationMinutes;          // 过期时间（分钟）
    
    public JwtService(
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-minutes}") long expirationMinutes
    ) {
        this.issuer = issuer;
        this.secret = secret;
        this.expirationMinutes = expirationMinutes;
    }

    // 生成令牌
    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant exp = now.plus(expirationMinutes, ChronoUnit.MINUTES);
        // 构建令牌
        return Jwts.builder()
                .issuer(issuer)
                .subject(String.valueOf(user.getId()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .signWith(key(), Jwts.SIG.HS256)
                .compact();
    }

    // 解析令牌
    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token);
    }

    // 生成密钥
    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}


