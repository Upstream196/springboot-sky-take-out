package com.sky.service;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CategoryService {
    //菜品添加
    int insertCategory( List<Category> category);

    //菜品删除
    int deleteCategory(List<Integer> ids);

    //菜品修改
    int updateCategory(List<Category> category);

    //分页查询
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    //新增分类
    void save(CategoryDTO categoryDTO);

    //根据类型查询分类
    List<Category> list(Integer type);
}
