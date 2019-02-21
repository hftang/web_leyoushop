package com.leyou.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author hftang
 * @date 2019-02-20 17:07
 * @desc 传输对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    @NotNull
    private Long addressId;//收货人地址
    @NotNull
    private Integer paymentType;//付款类型
    @NotNull
    private List<CartDTO> carts;// 订单详情
}