package com.leyou.item.api;

import com.leyou.item.pojo.Brand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author hftang
 * @date 2019-01-25 13:52
 * @desc
 */
public interface BrandApi {

    @GetMapping("/brand/{id}")
    Brand queryBrandById(@PathVariable("id") Long id);

    //根据ids 查询所有brand
    @GetMapping("/brand/list")
     List<Brand> queryBrandsByIds(@RequestParam("ids") List<Long> ids);
}
