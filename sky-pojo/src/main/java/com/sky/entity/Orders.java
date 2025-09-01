package com.sky.entity;

import lombok.Data;

@Data
public class Orders {
    private Long id;
    //下单用户id
    private Long userId;
    //地址Id
    private Long addressBookId;
    //用户名
    private String username;
    //手机号
    private String phone;
    //地址
    private String address;
    //收货人
    private String consignee;
    //订单号
    private String number;
    //订单状态
    private Integer status;


}
