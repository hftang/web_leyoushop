package com.leyou.order.utils;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.order.config.PayConfig;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.enums.PayState;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderStatus;
import com.leyou.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hftang
 * @date 2019-02-22 9:39
 * @desc 支付
 */
@Slf4j
@Component
public class PayHelper {

    @Autowired
    private WXPay wxPay;
    @Autowired
    private PayConfig config;


//    @Autowired
//    private StringRedisTemplate redisTemplate;

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderStatusMapper orderStatusMapper;

    public PayHelper(PayConfig payConfig) {
        // 真实开发时
        wxPay = new WXPay(payConfig);
        // 测试时
        // wxPay = new WXPay(payConfig, WXPayConstants.SignType.MD5, true);
    }

    public String createPayUrl(Long orderId, Long totalPay, String desc) {
//        String key = "ly.pay.url." + orderId;
//        try {
//            String url = this.redisTemplate.opsForValue().get(key);
//            if (StringUtils.isNotBlank(url)) {
//                return url;
//            }
//        } catch (Exception e) {
//            logger.error("查询缓存付款链接异常,订单编号：{}", orderId, e);
//        }

        try {
            Map<String, String> data = new HashMap<>();
            // 商品描述
            data.put("body", desc);//"乐优商城测试"
            // 订单号
            data.put("out_trade_no", orderId.toString());
            //货币
            data.put("fee_type", "CNY");
            //金额，单位是分 totalPay.toString()
            data.put("total_fee", 1 + "");
            //调用微信支付的终端IP（estore商城的IP）
            data.put("spbill_create_ip", "127.0.0.1");
            //回调地址
            data.put("notify_url", config.getNotifyUrl());
            // 交易类型为扫码支付
            data.put("trade_type", "NATIVE");
            //商品id,使用假数据
            data.put("product_id", "1234567");

            Map<String, String> result = this.wxPay.unifiedOrder(data);


            //判断通信标识
            isSuccess(result);
            log.info("[生成付款链接]：：" + result.get("code_url"));

            return result.get("code_url");

//            for(Map.Entry<String,String> entry: result.entrySet()){
//                String key = entry.getKey();
//                System.out.println("key:::"+key+"value:::"+entry.getValue());
//
//            }
//
//            if ("SUCCESS".equals(result.get("return_code"))) {
//                String url = result.get("code_url");
//                // 将付款地址缓存，时间为10分钟
////                try {
////                    this.redisTemplate.opsForValue().set(key, url, 10, TimeUnit.MINUTES);
////                } catch (Exception e) {
////                    logger.error("缓存付款链接异常,订单编号：{}", orderId, e);
////                }
//                return url;
//            } else {
//                log.error("[微信下单]创建预交易订单失败，错误信息：{}", result.get("return_msg"));
//                throw new LyException(ExceptionEnum.WX_PAY_ORDER_FAILED);
//
//            }
        } catch (Exception e) {
            log.error("创建预交易订单异常", e);
            return null;
        }
    }

    public void isSuccess(Map<String, String> result) {
        String return_code = result.get("return_code");

        if ("FAIL".equals(return_code)) {
            log.info("[微信下单] 通信失败：" + result.get("return_msg"));
            throw new LyException(ExceptionEnum.WX_PAY_NOTIFY_PARAM_ERROR);
        }

        //判断业务标识号
        String result_code = result.get("result_code");

        if ("FAIL".equals(result_code)) {
            log.info("[微信下单] 业务代码失败");
            throw new LyException(ExceptionEnum.WX_PAY_ORDER_FAILED);
        }
    }

    public void isValidSign(Map<String, String> data) {

        //重生生成签名
        try {
            String sign01 = WXPayUtil.generateSignature(data, config.getKey(), WXPayConstants.SignType.HMACSHA256);
            String sign02 = WXPayUtil.generateSignature(data, config.getKey(), WXPayConstants.SignType.MD5);

            String sign1 = data.get("sign");
            // 与传过来的签名 进行比较
            if (!StringUtils.equals(sign1, sign01) && !StringUtils.equals(sign1, sign02)) {
                //签名错误 抛出
                log.info("[回调签名失败]");
                throw new LyException(ExceptionEnum.INVALID_SIGN_ERROR);

            }


        } catch (Exception e) {
            log.info("[回调签名失败]");
            throw new LyException(ExceptionEnum.INVALID_SIGN_ERROR);
        }


    }

    /***
     * 回调后 本地数据库没有查到 支付订单状态 去 微信端 核实查询
     * @param orderId
     */

    public PayState queryPayState(Long orderId) {

        Map<String, String> data = new HashMap<>();
        data.put("out_trade_no", orderId.toString());

        try {
            Map<String, String> result = wxPay.orderQuery(data);
            //校验状态
            isSuccess(result);
            //校验签名
            isValidSign(result);
            //校验金额
            //3校验金额是否对
            String total_fee = result.get("total_fee");
            if (StringUtils.isEmpty(total_fee)) {
                throw new LyException(ExceptionEnum.ORDER_PARAM_ERROR);
            }
            //返回订单的金额
            long totalFee = Long.parseLong(total_fee);
            //得到id 去查
            Order order = this.orderMapper.selectByPrimaryKey(orderId);
            //金额
            Long actualPay = order.getActualPay();
            //4查到的金额 和 回调回来的金额做比较 actualPay
            if (totalFee != 1) {
                //金额不符
                throw new LyException(ExceptionEnum.ORDER_PARAM_ERROR);
            }

            /**
             * Success 支付成功
             * Refund 转入退款
             * notpay 未支付
             * closed 已关闭
             * revoked 已撤销
             * userpaying
             * payerror
             */
            String state = result.get("trade_state");
            if ("SUCCESS".equals(state)) {
                OrderStatus orderStatus = new OrderStatus();
                orderStatus.setStatus(OrderStatusEnum.UNPAY.value());
                orderStatus.setOrderId(orderId);
                orderStatus.setPaymentTime(new Date());
                int count = orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
                if (count != 1) {
                    throw new LyException(ExceptionEnum.ORDER_STATUS_EXCEPTION);
                }
                return PayState.SUCCESS;
            }

            if ("NOTPAY".equals(state) || "USERPAYING".equals(state)) {
                return PayState.NOT_PAY;
            }

            //其他情况 都是失败

            return PayState.FAIL;


        } catch (Exception e) {
            e.printStackTrace();
            //失败
            return PayState.NOT_PAY;
        }


    }

}
