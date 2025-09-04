package com.sky.controller.adimin;

import com.sky.entity.Category;
import com.sky.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
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


}
