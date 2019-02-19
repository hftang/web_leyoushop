package com.leyou.auth.client;

import com.leyou.user.api.UserApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author hftang
 * @date 2019-02-18 17:02
 * @desc
 */

@FeignClient("user-service")
public interface UserClient extends UserApi {


}
