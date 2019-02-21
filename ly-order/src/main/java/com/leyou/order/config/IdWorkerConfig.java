package com.leyou.order.config;

import com.leyou.utils.IdWorker;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hftang
 * @date 2019-02-20 20:01
 * @desc
 */
@Configuration
@EnableConfigurationProperties(IdWorkerProperties.class)
public class IdWorkerConfig {

    /***
     * 把他注入
     * @param props
     * @return
     */
    @Bean
    public IdWorker idWorker(IdWorkerProperties props) {
        return new IdWorker(props.getWorkerId(), props.getDataCenterId());
    }
}
