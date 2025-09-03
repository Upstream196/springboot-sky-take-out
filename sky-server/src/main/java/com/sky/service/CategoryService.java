package com.sky.service;

import com.sky.entity.Category;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CategoryService {
    //菜品添加
    int insertCategory( List<Category> category);
}
