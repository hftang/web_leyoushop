package com.leyou.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

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
