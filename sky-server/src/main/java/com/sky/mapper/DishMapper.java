package com.sky.mapper;


import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {

    @AutoFill(OperationType.INSERT)
    void save(Dish dish);

    /**
     * 根据分类id查询菜品数量
     */
    @Select("select count(id) from dish where categroy_id=#{categoryId}") //count的作业是什么？只查询id列吗？
    Integer countByCategoryId(Long categoryId);
}
