package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @author hftang
 * @date 2019-01-16 10:20
 * @desc
 * zuul网关的启动类
 */
@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
public class LyApiGate {

    public static void main(String[] args) {
        SpringApplication.run(LyApiGate.class, args);
    }
}
