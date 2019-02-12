package leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

        return ResponseEntity.ok(goodsService.querySpuByPage(page, rows, saleable, key));
    }

    /***
     * 保存商品（商品的新增）
     * @param spu
     * @return
     */
    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu) {
        goodsService.saveGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据spuid 查询 spudetail
     *
     * @param spuid
     * @return
     */

    @GetMapping("/spu/detail/{id}")
    public ResponseEntity<SpuDetail> queryDetailById(@PathVariable("id") Long spuid) {
        return ResponseEntity.ok(goodsService.queryDetailById(spuid));
    }

    /***
     * 根据spu查询下面所有的sku
     * @param spuid
     * @return
     */

    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> querySkuById(@RequestParam("id") Long spuid) {

        return ResponseEntity.ok(goodsService.querySkuListBySpuId(spuid));

    }

    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody Spu spuObj) {
        goodsService.updateGoods(spuObj);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 根据id 查询spu
     * @param id
     * @return
     */

    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id){
        return  ResponseEntity.ok(goodsService.querySpuById(id));
    }




}
