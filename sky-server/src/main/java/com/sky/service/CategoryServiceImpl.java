package com.sky.service;

import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;//1.添加CategoryMapper类型是为了后续调用mapper层的方法吗？
                                          //2.@Autowired注解的作用是什么？
    @Override
    public int insertCategory(List<Category> category) {
        return categoryMapper.insertCategory(category);//是否返回影响的行数？
    }
}
