package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author hftang
 * @date 2019-02-11 15:05
 * @desc
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class LyPageApplication {

    public static void main(String[] args) {

        SpringApplication.run(LyPageApplication.class);
    }

}
