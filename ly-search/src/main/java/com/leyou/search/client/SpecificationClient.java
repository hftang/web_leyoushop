package com.leyou.search.client;

import com.leyou.item.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author hftang
 * @date 2019-01-25 13:58
 * @desc
 */
@FeignClient("item-service")
public interface SpecificationClient extends SpecificationApi {


}
