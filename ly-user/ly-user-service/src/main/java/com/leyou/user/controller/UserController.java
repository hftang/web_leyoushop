package com.leyou.user.controller;

import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

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
    public ResponseEntity<Void> sendVerifyCode(String phone) {
        this.userService.sendVerifyCode(phone);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 注册
     *
     * @param user
     * @param code
     * @return
     */

    @PostMapping("register")
    public ResponseEntity<Void> regist(@Valid User user, BindingResult bindingResult, @RequestParam("code") String code) {
        //获取到user 字段验证错误信息
//        if (bindingResult.hasFieldErrors()) {
//            throw new RuntimeException(bindingResult.getFieldErrors().stream()
//                    .map(e -> e.getDefaultMessage()).collect(Collectors.joining("|")));
//        }
        Boolean boo = this.userService.regist(user, code);
        if (boo == null || !boo) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("query")
    public ResponseEntity<User> queryUser(@RequestParam("username") String username, @RequestParam("password") String password) {
        User user = this.userService.query(username, password);

        return ResponseEntity.ok(user);
    }


}
