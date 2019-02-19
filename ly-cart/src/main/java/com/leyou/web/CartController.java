package com.leyou.web;

import com.leyou.pojo.Cart;
import com.leyou.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    private ResponseEntity<Void> addCart(@RequestBody Cart cart) {
            this.cartService.addCart(cart);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
