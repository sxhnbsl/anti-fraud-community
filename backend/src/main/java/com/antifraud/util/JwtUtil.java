package com.antifraud.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT（JSON Web Token）工具类
 * 
 * 功能说明：
 * 1. 生成JWT令牌
 * 2. 从令牌中提取用户名
 * 3. 验证令牌的有效性
 * 
 * 使用场景：
 * - 用户登录后生成令牌
 * - 后续请求携带令牌进行身份验证
 * - 令牌过期后需要重新登录
 * 
 * @author 反诈骗平台开发团队
 * @version 1.0
 */
@Component
public class JwtUtil {
    /**
     * JWT密钥（从配置文件中读取）
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * JWT过期时间（毫秒，从配置文件中读取）
     */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * 生成JWT令牌
     * 
     * @param username 用户名
     * @return JWT令牌字符串
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 从JWT令牌中提取用户名
     * 
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * 验证JWT令牌的有效性
     * 
     * @param token JWT令牌
     * @return true表示令牌有效，false表示令牌无效或已过期
     */
    public boolean validateToken(String token) {
        try {
            System.out.println("Validating token with secret: " + secret);
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            System.out.println("Token validation successful");
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Token validation failed: Token expired - " + e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            System.out.println("Token validation failed: Unsupported JWT - " + e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            System.out.println("Token validation failed: Malformed JWT - " + e.getMessage());
            return false;
        } catch (SignatureException e) {
            System.out.println("Token validation failed: Invalid signature - " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.out.println("Token validation failed: Illegal argument - " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Token validation failed: Unknown error - " + e.getClass().getName() + " - " + e.getMessage());
            return false;
        }
    }
}