package com.leyou.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hftang
 * @date 2019-02-20 19:56
 * @desc
 */
@Data
@ConfigurationProperties(prefix = "leyou.worker")
public class IdWorkerProperties {

    private Long workerId; //当前机器id
    private long dataCenterId; //序列号
}
