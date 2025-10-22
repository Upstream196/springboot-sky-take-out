package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DishService {

    void save(DishDTO dishDTO);

    //这个接口为什么使用PageResult类型，这个类型的作用是什么？
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void deleteBatch(List<Long> ids);

    void startOrStop(Integer status, Long id);

    //根据id查询菜品和对应的口味数据
    DishVO getByIdWithFlavor(Long id);

    //根据id修改菜品基本信息和对应的口味信息
    void updateWithFlavor(DishDTO dishDTO);
}
