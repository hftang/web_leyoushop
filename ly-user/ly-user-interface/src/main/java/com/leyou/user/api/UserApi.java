package com.leyou.user.api;

import com.leyou.user.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author hftang
 * @date 2019-02-18 16:58
 * @desc
 */
public interface UserApi {

    @GetMapping("query")
    User queryUserByUserNameAndPassword(@RequestParam("username") String username,
                                        @RequestParam("password") String password);
}
