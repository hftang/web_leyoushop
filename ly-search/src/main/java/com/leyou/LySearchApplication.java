package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author hftang
 * @date 2019-01-24 21:45
 * @desc
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class LySearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(LySearchApplication.class, args);
    }

}
