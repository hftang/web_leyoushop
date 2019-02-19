package com.leyou.config;

import com.leyou.filter.UserIntercepter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cglib.transform.impl.AddInitTransformer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author hftang
 * @date 2019-02-19 17:11
 * @desc
 */
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtProperties props;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加 让注册中心添加 拦截器
        registry.addInterceptor(new UserIntercepter(props)).addPathPatterns("/**");

    }
}
