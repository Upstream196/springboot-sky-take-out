package com.sky.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class Dish implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    //菜品名称
    private String name;
    //菜品分类
    private Long categoryId;
    //菜品价格
    private BigDecimal price;//BigDecimal是什么类型
    //图片
    private String image;
    //描述信息
    private String description;
    //0停售 1起售
    private Long status;

    private LocalDateTime createTime;//LocalDateTime是什么类型

    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;
}
