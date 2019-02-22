package com.leyou.order.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author hftang
 * @date 2019-02-21 9:51
 * @desc
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {


}
