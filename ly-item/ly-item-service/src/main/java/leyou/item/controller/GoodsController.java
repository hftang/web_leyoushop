package leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Spu;
import leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hftang
 * @date 2019-01-22 14:07
 * @desc 查询商品
 */
@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /****
     * 分页查询商品
     * @param page
     * @param rows
     * @param saleable
     * @param key
     * @return
     */

    @GetMapping("spu/page")
    public ResponseEntity<PageResult<Spu>> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "key", required = false) String key) {



        return ResponseEntity.ok(goodsService.querySpuByPage(page,rows,saleable,key));
    }


}
