package com.leyou.filters;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.config.FilterProperties;
import com.leyou.config.JwtProperties;
import com.leyou.utils.CookieUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hftang
 * @date 2019-02-19 10:53
 * @desc zuul的请求过滤器 校验用户是否登录 以及登录后的token是否失效
 */
@Slf4j
@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class AuthFilter extends ZuulFilter {

    @Autowired
    private JwtProperties props;
    @Autowired
    private FilterProperties filterProps;

    @Override
    public String filterType() { //过滤类型
        return FilterConstants.PRE_TYPE;//前置过滤
    }

    @Override
    public int filterOrder() {
        return FilterConstants.FORM_BODY_WRAPPER_FILTER_ORDER - 1;//过滤顺序
    }

    @Override
    public boolean shouldFilter() { //是否过滤 true 是过滤 false 是不过滤
        //获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();

        //获取 request
        HttpServletRequest request = ctx.getRequest();

        //获取请求路径
        String requestURI = request.getRequestURI();

        boolean isAllowPath = isAllowPath(requestURI);

        //判断是否在白名单中

        return !isAllowPath;
    }

    private boolean isAllowPath(String requestURI) {

        for (String allowPath : filterProps.getAllowPaths()) {

            if (requestURI.startsWith(allowPath)) {
               return true;
            }

        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        //获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();

        //获取 request
        HttpServletRequest request = ctx.getRequest();

        //获取token
        String token = CookieUtils.getCookieValue(request, props.getCookieName());

        //解析token
        try {
            UserInfo user = JwtUtils.getUserInfo(props.getPublicKey(), token);
            log.info("[apiGate] 放行------》");
        } catch (Exception e) {
            //解析失败 未登录 拦截
            ctx.setSendZuulResponse(false);
            //返回码 403
            ctx.setResponseStatusCode(403);

            log.info("非法访问，未登录，地址：{}", request.getRemoteHost(), e );
        }

        //校验权限


        return null;
    }
}
