package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Employee;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.EmployeeMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
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
    //普通对象无需添加依赖注解，只有spring管理的bean需要添加
    private CategoryPageQueryDTO categoryPageQueryDTO;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 根据id删除分类
     * @param id
     * @return
     */
    @Override
    public void deleteCategory(Long id) {
        //删除逻辑
        //1.当前分类是否关联了菜品，若是则抛出业务异常
        Integer count = dishMapper.countByCategoryId(id);
        if(count >0){
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        //2.查询当前分类是否关联套餐，若是也抛出业务异常
        count = setmealMapper.countByCategoryId(id);
        if (count >0){
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        //删除分类数据
        categoryMapper.deleteCategory(id);

    }

    /**
     * 分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        //设置分页架构
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());

        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);

        long total = page.getTotal();
        List<Category> records = page.getResult();

        return new PageResult(total,records);
    }

    /**
     * 新增分类
     * @param categoryDTO
     */
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

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    @Override
    public List<Category> list(Integer type) {
        return categoryMapper.list(type);
    }

    /**
     * 修改分类
     * @param categoryDTO
     */
    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);

        categoryMapper.update(category);
    }

    /**
     * 启用禁用分类
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        categoryMapper.update(category);
    }


}
