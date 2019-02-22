package com.leyou.order.web;

import com.leyou.order.dto.OrderDTO;
import com.leyou.order.pojo.Order;
import com.leyou.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author hftang
 * @date 2019-02-20 17:21
 * @desc
 */
@RestController
@RequestMapping("order")
public class UserController {

    @Autowired
    private OrderService orderService;


    /**
     * 创建订单
     *
     * @param orderDTO
     * @return
     */
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderDTO orderDTO) {

        return ResponseEntity.ok(this.orderService.createOrder(orderDTO));
    }

    /***
     * 根据id 返回 order
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Order> queryOrderByOrderId(@PathVariable("id") Long id) {

        return ResponseEntity.ok( this.orderService.queryOrderByOrderId(id));
    }

}
