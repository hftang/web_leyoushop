package com.leyou.order.config;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hftang
 * @date 2019-02-22 9:30
 * @desc
 */
@Configuration
public class WXPayConfiguration {

    //注入配置

    @Bean
    @ConfigurationProperties(prefix = "leyou.pay")
    public PayConfig payConfig() {
        return new PayConfig();
    }

    //注入 wxPay

    @Bean
    public WXPay wxPay(PayConfig payConfig) {
        return new WXPay(payConfig, WXPayConstants.SignType.HMACSHA256);
    }


}
