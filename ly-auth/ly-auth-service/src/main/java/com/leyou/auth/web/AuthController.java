package com.leyou.auth.web;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.service.AuthService;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author hftang
 * @date 2019-02-18 16:16
 * @desc 登录授权功能
 */
@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties props;

//    @Value("leyou.jwt.cookieName ")
//    private String cookieName;

    @PostMapping("login")
    public ResponseEntity<Void> login(@RequestParam("username") String userName,
                                      @RequestParam("password") String password,
                                      HttpServletResponse response, HttpServletRequest request) {

        //1登录
        String token = this.authService.login(userName, password);
        //2写入cookie

        if (StringUtils.isBlank(token)) {
            throw new LyException(ExceptionEnum.USERNAME_OR_PASSWORD_ERROR);
        }
        //将Token写入cookie中
        CookieUtils.newBuilder(response).httpOnly().request(request).build(props.getCookieName(), token);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
