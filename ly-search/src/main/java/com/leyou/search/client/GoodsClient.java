package com.leyou.search.client;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.api.GoodsApi;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author hftang
 * @date 2019-01-25 10:38
 * @desc 这里 和item-service 组合 提供了另一半的服务
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {

//   @GetMapping("/spu/detail/{id}")
//    SpuDetail queryDetailById(@PathVariable("id") Long spuid);
//
//    @GetMapping("/sku/list")
//    List<Sku> querySkuById(@RequestParam("id") Long spuid);
//
//    @GetMapping("/spu/page")
//    PageResult<Spu> querySpuByPage(
//            @RequestParam(value = "page", defaultValue = "1") Integer page,
//            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
//            @RequestParam(value = "saleable", required = false) Boolean saleable,
//            @RequestParam(value = "key", required = false) String key);




}
