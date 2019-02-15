package com.leyou.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hftang
 * @date 2019-02-14 15:29
 * @desc 通过这个类去配置文件中获取到对应的值 yml
 */
@ConfigurationProperties(prefix = "ly.sms")
@Data
public class SmsProperties {

    String accessKeyId;
    String accessKeySecret;
    String signName;
    String verifyCodeTemplate;
}
