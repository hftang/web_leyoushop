package com.leyou.client;

import com.leyou.item.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author hftang
 * @date 2019-01-25 13:59
 * @desc
 */
@FeignClient("item-service")
public interface BrankClient extends BrandApi {

}
