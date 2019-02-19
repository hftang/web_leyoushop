package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.user.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * @author hftang
 * @date 2019-02-18 16:17
 * @desc
 */
@Slf4j
@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {

    @Autowired
    private JwtProperties props;

    @Autowired
    private UserClient userClient;

    public String login(String userName, String password) {
        try {
            //1 校验用户名与密码
            User user = userClient.queryUserByUserNameAndPassword(userName, password);
            if (user == null) {
                throw new LyException(ExceptionEnum.USERNAME_OR_PASSWORD_ERROR);
            }
            //2 生成 token
            String token = JwtUtils.generateToken(new UserInfo(user.getId(), userName), props.getPrivateKey(), props.getExpire());
            return token;
        } catch (Exception e) {
            log.info("[token authService]: token 生成失败！账号或者密码错误！！！！");
            throw new LyException(ExceptionEnum.USERNAME_OR_PASSWORD_ERROR);
        }

    }
}
