package leyou.item.service;

import com.leyou.item.pojo.Category;
import leyou.item.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hftang
 * @date 2019-01-16 14:19
 * @desc
 */

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> queryCategoryById(Long pid) {
        Category category = new Category();
        category.setParentId(pid);

        System.out.println("--------->service:"+pid);

        List<Category> list = this.categoryMapper.select(category);

        return list;
    }
}
