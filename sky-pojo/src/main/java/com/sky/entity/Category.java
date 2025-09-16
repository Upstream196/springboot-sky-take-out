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

    private LocalDateTime create_time;

    private LocalDateTime update_time;

    private Long create_user;

    private Long update_user;
}
