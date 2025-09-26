package com.sky.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Employee;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    @Override
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        //属性拷贝
        BeanUtils.copyProperties(categoryDTO, category);

        //分类状态默认为禁用状态0
        category.setStatus(StatusConstant.DISABLE);

        //设置创建时间，修改时间，创建人，修改人
//        category.setCreate_time(LocalDateTime.now());
//        category.setUpdate_time(LocalDateTime.now());
//        category.setCreate_user(BaseContext.getCurrentId());
//        category.setUpdate_user(BaseContext.getCurrentId());

        categoryMapper.insert(category);

//        categoryMapper.insertCategory(category);
//        'insertCategory(java.util.List<com.sky.entity.Category>)' in 'com.sky.mapper.CategoryMapper' cannot be applied to '(com.sky.entity.Category)'
    //若不添加insert方法按照insertCategory方法添加集合类该如何传参？
    }

    @Override
    public List<Category> list(Integer type) {
        return categoryMapper.list(type);
    }




}
