package com.leyou.search.client;

import com.leyou.item.api.CategoryApi;
import com.leyou.item.pojo.Category;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author hftang
 * @date 2019-01-25 10:12
 * @desc
 */

/**
 * spring:
 * application:
 * name: item-service
 * feignClient 微服务的主机名称
 */
@FeignClient("item-service")
public interface CategoryClient extends CategoryApi {

    /***
     * 根据分类id 查询 分类
     * @param ids
     * @return
     */

//    @GetMapping("/category/list/ids")
//    List<Category> queryCategorysByIds(@RequestParam("ids") List<Long> ids);
}
