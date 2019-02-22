package com.leyou.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hftang
 * @date 2019-02-20 17:10
 * @desc 传输的 购物车对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {

    private Long skuId; //商品的skuid
    private Integer num; //购买数量
}
