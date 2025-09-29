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

        // 测试1：生成JWT
        System.out.println("📝 测试1：生成JWT令牌");
        String myJwt = MyJwtUtil.createMyJWT(secretKey, ttlMillis, claims);

        if (myJwt != null) {
            // 测试2：解析JWT
            System.out.println("🔍 测试2：解析JWT令牌");
            MyJwtUtil.parseMyJWT(secretKey, myJwt);

            // 测试3：错误密钥测试
            System.out.println("🐛 测试3：错误密钥测试");
            MyJwtUtil.parseMyJWT("wrong-key", myJwt);
        }
    }
}