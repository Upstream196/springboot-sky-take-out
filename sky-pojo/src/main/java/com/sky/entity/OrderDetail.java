package com.sky.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
@Data
public class OrderDetail  implements Serializable { //->Serializable这个接口的作用是什么？

    private static final long serialVersionUID = 1L;//->这段代码的作用是什么？

    private Long id;
    //名称
    private String name;
    //订单id
    private Long orderId;
    //菜品id
    private Long dishId;
    //套餐
    private Long setmealId;
    //口味
    private String dishFlavor;
    //数量
    private Integer number;
    //金额
    private BigDecimal amount;
    //图片
    private String image;
}
