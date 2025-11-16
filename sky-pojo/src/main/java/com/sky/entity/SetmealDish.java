package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetmealDish  implements Serializable {

    private static final long serialVsersionUID = 1L;

    private Long id;
    //套餐id
    private Long setmealId;
    //菜品id
    private Long dishId;
    //菜品名称
    private String name;
    //菜品原价
    private BigDecimal price;
    //份数
    private Integer copies;
}
