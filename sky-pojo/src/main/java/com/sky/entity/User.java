package com.sky.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    //微信用户唯一标识
    private String openid;
    //姓名
    private String name;
    //手机号
    private String phone;
    //性别 0女 1男
    private String sex;
    //身份证号
    private String idNumber;
    //头像
    private String avatar;
    //注册时间
    private LocalDateTime createTime;
}
