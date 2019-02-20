package com.leyou.web;

import com.leyou.common.exception.LyException;
import com.leyou.pojo.Cart;
import com.leyou.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author hftang
 * @date 2019-02-19 17:33
 * @desc
 */

@RestController
public class CartController {

    @Autowired
    private CartService cartService;


    /***
     * 新增购物车
     * @param cart
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart) {
        this.cartService.addCart(cart);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 查询所有购物车中的商品 从redis中获取
     *
     * @return
     */

    @GetMapping
    public ResponseEntity<List<Cart>> queryCartList() {

        List<Cart> carts = this.cartService.queryCartList();

        if (CollectionUtils.isEmpty(carts)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(carts);
    }

    /**
     * 更改 购物车中商品的数量
     *
     * @param skuId
     * @param num
     * @return
     */

    @PutMapping
    public ResponseEntity<Void> updateCartNum(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num) {

        this.cartService.updateCartNum(skuId, num);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 删除指定的购物车
     * @param skuId
     * @return
     */

    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId") Long skuId){
        this.cartService.deleteCartByskuId(skuId);
        return ResponseEntity.ok().build();

    }


}
