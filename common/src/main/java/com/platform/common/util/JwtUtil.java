package com.platform.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

/**
 * JWT 工具类，生成和解析 Token
 */
public class JwtUtil {
    /**
     * 签名密钥（建议放配置中心）
     */
    private static final String SECRET = "trade-platform-very-secret-key-1234567890";
    /**
     * Token 有效期（毫秒）
     */
    private static final long EXPIRATION = 1000 * 60 * 60 * 24; // 1天

    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    /**
     * 生成 JWT Token
     * 
     * @param username 用户名
     * @return token
     */
    public static String generateToken(Long userId, String username) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key)
                .compact();
    }

    public static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    public static Long getUserId(String token) {
        return getClaims(token).get("userId", Long.class);
    }
}