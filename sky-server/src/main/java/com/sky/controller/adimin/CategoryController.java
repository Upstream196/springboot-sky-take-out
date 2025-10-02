package com.sky.controller.adimin;

import com.sky.dto.CategoryDTO;
import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Api(tags = "分类相关接口")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping("/insert")
    public String insertCategory(@RequestBody List<Category> category) {
        int result = categoryService.insertCategory(category);
        System.out.println(">>> insertCategory 被调用，size = " + category.size());
        if(result > 0) {
            return "sucess";
        }
        else {
            return "error";
        }
    }

    @DeleteMapping("/delete")
    public String deleteCategory(@RequestBody List<Integer> ids) {
        int result = categoryService.deleteCategory(ids);
        if(result > 0) {
            return "sucess";
        }
        else {
            return "error";
        }
    }

    @PutMapping("/update")
    public String updateCategory(@RequestBody List<Category> category) {
        int result = categoryService.updateCategory(category);
        if(result > 0) {
            return "sucess";
        }
        else {
            return "error";
        }
    }

    @PostMapping
    @ApiOperation("新增分类")
    public Result<String> save(@RequestBody CategoryDTO categoryDTO) {
        log.info(">>> CategoryController.save() 线程={}", Thread.currentThread().getName());

       log.info("新增分类：{}",categoryDTO);//Cannot resolve symbol 'log'->@Slf4j添加该注解解决原因？方法参数为什么不选择实体类？
        categoryService.save(categoryDTO);
        return Result.success();
    }

    //根据类型查询分类
    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List<Category>> list(Integer type){
        List<Category> list=categoryService.list(type);
        return Result.success(list);
    }

}
