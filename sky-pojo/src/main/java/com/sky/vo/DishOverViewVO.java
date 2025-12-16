package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 菜品总览
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DishOverViewVO implements Serializable {

    //已开售数量
    private Integer sold;

    //已停售数量
    private Integer discontinued;
}
