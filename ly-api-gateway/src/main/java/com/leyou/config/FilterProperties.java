package com.leyou.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author hftang
 * @date 2019-02-19 11:18
 * @desc 拦截白名单
 */
@Data
@ConfigurationProperties(prefix = "leyou.filter")
public class FilterProperties {
    private List<String> allowPaths;
}
