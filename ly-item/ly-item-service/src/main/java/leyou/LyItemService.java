package leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author hftang
 * @date 2019-01-16 11:28
 * @desc item-service 的启动器
 */

@SpringBootApplication
@EnableDiscoveryClient
public class LyItemService {

    public static void main(String[] args) {
        SpringApplication.run(LyItemService.class, args);
    }


}
