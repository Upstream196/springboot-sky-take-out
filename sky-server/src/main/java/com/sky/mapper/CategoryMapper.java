package com.sky.mapper;

import com.sky.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {
    /**
     * 设置菜品添加，删除与更新的方法
     */
    //添加菜品分类，返回影响的行数
    int insertCategory(@Param("category") List<Category> category);

    //根据id删除菜品分类，返回影响的行数
    int deleteCategory(@Param("ids") List<Integer> ids);

    //批量更新菜品(更新名称),返回影响行数,根据id或用户名进行更改
    int updateCategory(@Param("list") List<Category> category);


}
