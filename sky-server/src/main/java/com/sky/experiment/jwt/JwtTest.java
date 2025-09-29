package com.sky.experiment.jwt;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试我的JWT实现
 */
public class JwtTest {

    public static void main(String[] args) {
        // 测试参数
        String secretKey = "my-secret-key";
        long ttlMillis = 3600000; // 1小时
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 12345);
        claims.put("username", "zhangsan");
        claims.put("role", "admin");

        System.out.println("🚀 开始JWT实验测试\n");

        // 测试：使用我的实现生成JWT
        System.out.println("📝 测试：生成JWT令牌");
        String myJwt = MyJwtUtil.createMyJWT(secretKey, ttlMillis, claims);

        if (myJwt != null) {
            System.out.println("✅ 实验成功！生成了JWT令牌");
            System.out.println("📋 生成的JWT: " + myJwt);
        } else {
            System.out.println("❌ 实验失败，请检查错误信息");
        }
    }
}