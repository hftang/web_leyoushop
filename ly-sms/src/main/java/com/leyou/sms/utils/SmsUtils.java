package com.leyou.sms.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.leyou.sms.config.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author hftang
 * @date 2019-02-14 15:34
 * @desc sms 的工具类
 */
@Slf4j
@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsUtils {

    @Autowired
    private SmsProperties properties;

    @Autowired
    private StringRedisTemplate redisTemplate;


    //产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";

    String KEY_PREFIX = "sms:phone";

    /**
     * 发短信的方法
     *
     * @param phoneNum
     * @param signName
     * @param templateCode
     * @param templateParam
     * @return
     * @throws ClientException
     */

    public SendSmsResponse sendSms(String phoneNum, String templateCode, String signName, String templateParam) {
        String key = KEY_PREFIX + phoneNum;

        //在每次发送短信前 做一下校验 同一个号码 发送短信是需要限流的

        String nowTime = redisTemplate.opsForValue().get(key);

        if (StringUtils.isNotBlank(nowTime)) {
            Long aLong = Long.valueOf(nowTime);
            if (System.currentTimeMillis() - aLong < 6000) {
                log.info("[短信服务] 短信发送频率过高了 拒绝发送");
                return null;
            }
        }

        try {
            //可自助调整超时时间
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");

            //初始化acsClient,暂不支持region化
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", properties.getAccessKeyId(), properties.getAccessKeySecret());
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);

            //组装请求对象-具体描述见控制台-文档部分内容
            SendSmsRequest request = new SendSmsRequest();
            request.setMethod(MethodType.POST);
            //必填:待发送手机号
            request.setPhoneNumbers(phoneNum);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName(signName);
            //必填:短信模板-可在短信控制台中找到"SMS_133976814"
            request.setTemplateCode(templateCode);
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为 "{\"code\":\"123\"}"
            request.setTemplateParam(templateParam);

            //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
            //request.setSmsUpExtendCode("90997");

            //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
//        request.setOutId("123456");
//
//        //hint 此处可能会抛出异常，注意catch
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

            if (!"OK".equals(sendSmsResponse.getCode())) {
                log.info("[短信服务] 发送短信失败, phoneNumber:{} , 原因：{}", phoneNum, sendSmsResponse.getMessage());
            }
            //短信发送成功的日志

            log.info("[短信发送成功] 手机号码：" + phoneNum);

            //发送短信成功后 把手机号码存到redis中  为了限流给同一个手机号 一分钟内 多次发送短信
            redisTemplate.opsForValue().set(key, String.valueOf(System.currentTimeMillis()), 1, TimeUnit.MINUTES);


            return sendSmsResponse;
        } catch (Exception e) {
            log.error("[发送短信异常]，原因：{}" + e.getMessage());
            return null;
        }
    }
}
