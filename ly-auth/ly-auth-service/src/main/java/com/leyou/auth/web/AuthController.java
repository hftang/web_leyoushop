package com.leyou.auth.web;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.service.AuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("verify")
    public ResponseEntity<UserInfo> verifyUser(@CookieValue("LY_TOKEN") String token, HttpServletRequest request, HttpServletResponse response) {

        try {
            //从token中解析token信息
            UserInfo userInfo = JwtUtils.getUserInfo(this.props.getPublicKey(), token);
            //解析成功后 重新刷新token
            String new_token = JwtUtils.generateToken(userInfo, props.getPrivateKey(), props.getExpire());

            //从新更新 cookie中的token
            CookieUtils.newBuilder(response).httpOnly().request(request).build(props.getCookieName(), new_token);


            if (userInfo != null) {
                return ResponseEntity.ok(userInfo);
            }
        } catch (Exception e) {

        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }


}
