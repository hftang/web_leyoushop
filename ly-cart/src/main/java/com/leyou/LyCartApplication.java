package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author hftang
 * @date 2019-02-19 15:55
 * @desc 购物车的启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class LyCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(LyCartApplication.class, args);
    }
}
