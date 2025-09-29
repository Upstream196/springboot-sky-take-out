package com.sky.experiment.jwt;

import io.jsonwebtoken.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * 我的JWT工具类实验实现
 * 学习目标：理解JWT令牌的生成原理
 */
public class MyJwtUtil {

    /**
     * 我的JWT生成方法 - 按照我的理解重新实现
     */
    public static String createMyJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        try {
            System.out.println("=== 开始生成JWT令牌 ===");
            System.out.println("密钥: " + secretKey);
            System.out.println("过期时间: " + ttlMillis + "ms");
            System.out.println("声明信息: " + claims);

            // 1. 选择签名算法 - HS256
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
            System.out.println("使用算法: " + signatureAlgorithm);

            // 2. 计算过期时间点
            long currentTime = System.currentTimeMillis();
            long expMillis = currentTime + ttlMillis;
            Date expDate = new Date(expMillis);

            System.out.println("当前时间: " + new Date(currentTime));
            System.out.println("过期时间: " + expDate);

            // 3. 构建JWT - 分步骤理解每个操作
            JwtBuilder builder = Jwts.builder();

            // 设置声明（用户信息）
            if (claims != null && !claims.isEmpty()) {
                builder.setClaims(claims);
                System.out.println("已设置声明信息");
            }

            // 设置签名
            builder.signWith(signatureAlgorithm, secretKey.getBytes(StandardCharsets.UTF_8));
            System.out.println("已设置签名");

            // 设置过期时间
            builder.setExpiration(expDate);
            System.out.println("已设置过期时间");

            // 4. 生成最终令牌
            String jwt = builder.compact();
            System.out.println("生成的JWT: " + jwt);
            System.out.println("JWT长度: " + jwt.length() + " 字符");
            System.out.println("=== JWT生成完成 ===\n");

            return jwt;

        } catch (Exception e) {
            System.err.println("生成JWT时出错: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}