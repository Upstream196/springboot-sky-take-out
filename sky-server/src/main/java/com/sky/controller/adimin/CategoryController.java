package com.sky.controller.adimin;

import com.sky.entity.Category;
import com.sky.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping("/insert")
    public String insert(@RequestBody List<Category> category) {
        int result = categoryService.insertCategory(category);
        System.out.println(">>> insertCategory 被调用，size = " + category.size());
        if(result > 0) {
            return "sucess";
        }
        else {
            return "error";
        }
    }
}
