package com.leyou.order.web;

import com.leyou.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hftang
 * @date 2019-02-22 11:25
 * @desc 微信支付成功回调
 */
@Slf4j
@RestController
@RequestMapping("notify")
public class NotifyController {

    @Autowired
    private OrderService orderService;

    /***
     * 返回的类型是 xml produces
     * @param result
     * @return
     */
    @PostMapping(value = "pay", produces = "application/xml")
    public String hello(@RequestBody Map<String, String> result) {
        log.info("[微信支付回调] result" + result.toString());


        //处理回调
        this.orderService.handleNotify(result);


        Map<String, String> msg = new HashMap<>();
        msg.put("return_code", "SUCCESS");
        msg.put("return_msg", "OK");


        return "";

    }

}
