package com.leyou.order.config;

import com.github.wxpay.sdk.WXPayConfig;
import lombok.Data;

import java.io.InputStream;

/**
 * @author hftang
 * @date 2019-02-22 9:12
 * @desc
 */
@Data
public class PayConfig implements WXPayConfig {

    private String appID = "";//公共账号id
    private String mchID = "";//商户号
    private String key = "";//生成签名秘钥
    private int httpConnectTimeoutMs = 1000;//连接超时
    private int httpReadTimeoutMs = 5000;//读取超时
    private String notifyUrl = "";//回调通知地址

    @Override
    public InputStream getCertStream() {
        return null;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return 0;
    }
}
