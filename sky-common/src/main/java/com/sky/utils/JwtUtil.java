package com.sky.utils;

import io.jsonwebtoken.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtil {
    /**
     * 生成jwt
     * 使用Hs256算法，私钥使用固定密钥
     * @param secretKey jwt密钥
     * @param ttlMillis jwt过期时间(毫秒)
     * @param claims    设置的信息
     * @return
     */
      public static String createJWT(String secretKey,long ttlMillis,Map<String, Object> claims){
        //配置签名算法
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        //设置令牌的生命周期
        long expMillis=System.currentTimeMillis()+ttlMillis;
        Date exp=new Date(expMillis);

        //构建JWT+数据组装
        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .signWith(signatureAlgorithm,secretKey.getBytes(StandardCharsets.UTF_8))
                .setExpiration(exp);
        //令牌生成
        return builder.compact();
    }
    /**
     * @param secretKey jwt秘钥 此秘钥一定要保留好在服务端, 不能暴露出去, 否则sign就可以被伪造, 如果对接多个客户端建议改造成多个
     * @param token     加密后的token
     * @return
     */
    public static Claims parseJWT(String secretKey,String token){
        //构建jwt解析器
        Claims claims= Jwts.parser()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))//密钥还是字符串形式，我们需要的是子节数组形式
                .parseClaimsJws(token).getBody();

        return  claims;
    }

    //返回值的类型决定方法类型
}
