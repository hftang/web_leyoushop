package leyou.item.controller;

import com.leyou.item.pojo.Category;
import leyou.item.service.CategoryService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author hftang
 * @date 2019-01-16 14:21
 * @desc
 */
@RestController
@RequestMapping("category")

public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoryById(@RequestParam("pid") Long pid) {

        List<Category> list = categoryService.queryCategoryById(pid);
        if (list == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(list);
    }


}
