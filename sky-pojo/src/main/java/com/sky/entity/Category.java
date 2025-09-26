package com.sky.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Category {

    private Integer id;

    private String name;

    private Integer type;

    private Integer sort;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;
}
