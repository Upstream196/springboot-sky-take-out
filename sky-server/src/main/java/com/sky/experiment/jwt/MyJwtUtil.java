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
   public static String createMyJWT(String secretKey, long ttlMillis, Map<String,Object> claims){

       //指定签名的时候使用的签名算法，即header部分
       SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
       //生成JWT的时间
       Long currentTimeMillis=System.currentTimeMillis();
       Long now=currentTimeMillis+ttlMillis;
       Date exp=new Date(now);

       //设置jwt的body
       JwtBuilder builder = Jwts.builder()
               .setClaims(claims)
               .signWith(signatureAlgorithm,secretKey.getBytes(StandardCharsets.UTF_8))
               .setExpiration(exp);

       return builder.compact();
   }
    /**
     * 我的JWT解析方法 - 学习解析过程
     */
    public static Claims parseMyJWT(String secretKey, String jwt) {
        try {
            System.out.println("=== 开始解析JWT令牌 ===");
            System.out.println("待解析JWT: " + jwt);

            Claims claims= Jwts.parser()
                            .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                            .parseClaimsJws(jwt)
                            .getBody();

            System.out.println("✅ 解析成功！");
            System.out.println("声明信息: " + claims);
            System.out.println("过期时间: " + claims.getExpiration());
            System.out.println("用户ID: " + claims.get("userId"));
            System.out.println("用户名: " + claims.get("username"));
            System.out.println("角色: " + claims.get("role"));
            System.out.println("=== JWT解析完成 ===\n");

            return claims;

        } catch (ExpiredJwtException e) {
            System.err.println("❌ JWT已过期: " + e.getMessage());
            return null;
        } catch (SignatureException e) {
            System.err.println("❌ 签名验证失败: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("❌ 解析JWT时出错: " + e.getMessage());
            return null;
        }
    }
}

