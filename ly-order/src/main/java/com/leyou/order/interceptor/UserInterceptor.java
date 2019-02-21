package com.leyou.order.interceptor;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.order.config.JwtProperties;
import com.leyou.utils.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author hftang
 * @date 2019-02-20 14:09
 * @desc 用户拦截器
 */
@Slf4j
public class UserInterceptor implements HandlerInterceptor {

    private JwtProperties jwtProperties;

    public UserInterceptor(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    private static ThreadLocal<UserInfo> tl = new ThreadLocal<>();//单线程的线程是安全的 存登录用户的信息

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //从request中获取用户
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());

        try {
            UserInfo userInfo = JwtUtils.getUserInfo(jwtProperties.getPublicKey(), token);

            tl.set(userInfo);

            //解析成功
            return true;//放行
        } catch (Exception e) {
            log.error("[order:user-interceptor] 获取用户失败：" + e.toString());

            return false; //拦截
        }


    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        //用完后删除
        tl.remove();
    }

    /**
     * 获取当前用户
     *
     * @return
     */

    public static UserInfo getUserInfo() {
        return tl.get();
    }
}
