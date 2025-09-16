package com.sky.dto;

import com.sky.entity.DishFlavor;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDTO implements Serializable {

    private Long id;
    //菜品名称
    private String name;
    //菜品分类id
    private Long categoryId;
    //菜品价格
    private BigDecimal price;//BigDecimal这是什么类型？它的作用是什么？
    //图片
    private String image;
    //描述信息
    private String description;
    //菜品状态 0停售 1起售
    private Integer status;
    //口味
    private List<DishFlavor> flavors=new ArrayList<>();//对象属性使用集合的作用是什么？
}
