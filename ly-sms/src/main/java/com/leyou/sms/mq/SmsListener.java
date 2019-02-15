package com.leyou.sms.mq;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.leyou.sms.config.SmsProperties;
import com.leyou.sms.utils.SmsUtils;
import com.sun.media.jfxmedia.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author hftang
 * @date 2019-02-14 16:07
 * @desc
 */
@Slf4j
@EnableConfigurationProperties(SmsProperties.class)
@Component
public class SmsListener {

    @Autowired
    private SmsUtils smsUtils;
    @Autowired
    private SmsProperties smsProperties;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.sms.queue", durable = "true"),
            exchange = @Exchange(value = "leyou.sms.exchange", type = ExchangeTypes.TOPIC),
            key = "sms.verify.code"
    ))
    public void listenerMsg(Map<String, String> msg) {
        log.info("[smslistener]:接受到消息"+msg);

        if (msg == null || msg.size() <= 0) {
            return;
        }

        String code = msg.get("code");
        String phone = msg.get("phone");

        if (StringUtils.isBlank(code) || StringUtils.isBlank(phone)) {
            return;
        }

        SendSmsResponse sendSmsResponse = smsUtils.sendSms(phone, code, smsProperties.getSignName(), smsProperties.getVerifyCodeTemplate());


    }


}
