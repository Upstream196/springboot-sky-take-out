package com.sky.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Employee;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;//1.添加CategoryMapper类型是为了后续调用mapper层的方法吗？
    @Autowired
    private EmployeeMapper employeeMapper;

    //2.@Autowired注解的作用是什么？
    @Override
    public int insertCategory(List<Category> category) {
        return categoryMapper.insertCategory(category);//是否返回影响的行数？
    }

    @Override
    public int deleteCategory(List<Integer> ids) {
        return categoryMapper.deleteCategory(ids);
    }

    @Override
    public int updateCategory(List<Category> category) {
        return categoryMapper.updateCategory(category);
    }

    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        return null;
    }


}
