package com.sky.entity;

import lombok.Data;

@Data
public class Category {

    private Integer id;

    private String name;

    private Integer type;

    private Integer sort;

    private Integer status;

    private String create_time;

    private String update_time;

    private Integer create_user;

    private Integer update_user;
}
