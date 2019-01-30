package com.leyou.item.api;

import com.leyou.item.pojo.Category;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author hftang
 * @date 2019-01-25 11:31
 * @desc 一样都提供一半服务 另一半让继承者去实现
 */
public interface CategoryApi {

    @GetMapping("/category/list/ids")
    List<Category> queryCategorysByIds(@RequestParam("ids") List<Long> ids);
}
