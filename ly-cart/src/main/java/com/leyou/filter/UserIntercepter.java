package com.leyou.filter;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.config.JwtProperties;
import com.leyou.utils.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author hftang
 * @date 2019-02-19 16:19
 * @desc 购物车的请求拦截器  拦截登录的用户 验证登录者的信息
 */
@Slf4j
public class UserIntercepter implements HandlerInterceptor {


    private JwtProperties props;

    //存到线程中 单线程是共享的
    static ThreadLocal<UserInfo> threadLocal = new ThreadLocal<>();

    public UserIntercepter(JwtProperties props) {
        this.props = props;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //从请求中获取 token
        String token = CookieUtils.getCookieValue(request, props.getCookieName());
        //解析token
        try {
            UserInfo userInfo = JwtUtils.getUserInfo(props.getPublicKey(), token);
            threadLocal.set(userInfo);

            return true;
        } catch (Exception e) {
            log.info("[cart购物车解析用户失败]：" + e.toString());
            //解析失败
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //用完数据 要删除数据

        threadLocal.remove();
    }

    public static UserInfo getUserInfo() {

        return threadLocal.get();
    }
}
