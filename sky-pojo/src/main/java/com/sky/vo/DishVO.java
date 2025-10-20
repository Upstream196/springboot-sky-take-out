package com.sky.vo;

import com.sky.entity.DishFlavor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DishVO implements Serializable {

    private Long id;  //这个id值代表什么？->菜品本身的id
    //菜品名称
    private String name;
    //菜品分类id
    private Long categoryId; //具有什么作用？
    //菜品价格
    private BigDecimal price;  //BigDecimal类型的作用是什么？
    //图片
    private String image;
    //描述信息
    private String description;
    //菜品状态 0停售 1起售
    private Integer status;
    //更新时间
    private LocalDateTime updateTime;
    //分类名称
    private String categoryName;
    //菜品关联的口味
    private List<DishFlavor> flavors=new ArrayList<>();
}
