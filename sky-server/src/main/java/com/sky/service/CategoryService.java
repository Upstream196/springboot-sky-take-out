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

    //菜品删除
    void deleteCategory(Long id);
    //分页查询
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);
    //新增分类
    void save(CategoryDTO categoryDTO);
    //根据类型查询分类
    List<Category> list(Integer type);
    //修改分类
    void update(CategoryDTO categoryDTO);
    //启用禁用分类
    void startOrStop(Integer status, Long id);
}
