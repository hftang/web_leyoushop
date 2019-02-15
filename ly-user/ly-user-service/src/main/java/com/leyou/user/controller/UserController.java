package com.leyou.user.controller;

import com.leyou.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hftang
 * @date 2019-02-15 10:46
 * @desc
 */
@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 校验数据是否可行
     */
    @GetMapping("check/{data}/{type}")
    public ResponseEntity<Boolean> checkUserData(@PathVariable("data") String data, @PathVariable(value = "type") Integer type) {
        log.info("check收到：data:" + data);
        Boolean boo = userService.checkData(data, type);
        if (boo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(boo);
    }

    /**
     * 发送短信验证码
     */

    @PostMapping("code")
    public ResponseEntity<Void> sendVerifyCode(String phone){
        this.userService.sendVerifyCode(phone);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
