package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author hftang
 * @date 2019-01-21 21:05
 * @desc
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {

    BRAND_CREATE_FAILED(500, "新增品牌失败"),
    BRAND_NOT_FOUND(404, "品牌查询失败"),
    UPDATE_BRAND_FAILED(500, "品牌更新失败"),
    DELETE_BRAND_EXCEPTION(500, "删除品牌失败"),

    GOODS_SAVE_ERROR(500, "新增商品错误"),
    GOODS_ID_ERROR(500, "商品Id不能为能空"),
    GOODS_NOT_FOUND(400, "商品未查询到"),
    GOODS_NOT_SALEABLE(400, "商品未上架"),
    GOODS_UPDATE_ERROR(500, "商品更新失败"),
    GOODS_DETAIL_ERROR(400, "商品详情不存在"),
    DELETE_GOODS_ERROR(500, "删除商品错误"),
    UPDATE_SALEABLE_ERROR(500, "更新商品销售状态错误"),
    STOCK_NOT_ENOUGH(500, "商品库存不足"),

    CATEGORY_NOT_FOUND(204, "分类未查询到"),
    STOCK_NOT_FOUND(204, "库存查询失败"),
    SPU_NOT_FOUND(201, "SPU未查询到"),
    SKU_NOT_FOUND(201, "SKU未查询到"),

    RECEIVER_ADDRESS_NOT_FOUND(400, "收获地址不存在"),
    ORDER_NOT_FOUND(400, "订单不存在"),
    ORDER_STATUS_EXCEPTION(500, "订单状态异常"),
    CREATE_PAY_URL_ERROR(500, "常见支付链接异常"),
    WX_PAY_SIGN_INVALID(400, "微信支付签名异常"),
    WX_PAY_NOTIFY_PARAM_ERROR(400, "微信支付回调参数异常"),

    INVALID_FILE_FORMAT(400, "文件格式错误"),
    UPLOAD_IMAGE_EXCEPTION(500, "文件上传异常"),
    INVALID_PARAM(400, "参数错误"),
    USERNAME_OR_PASSWORD_ERROR(400, "账号或密码错误"),
    VERIFY_CODE_NOT_MATCHING(400, "验证码错误"),
    PASSWORD_NOT_MATCHING(400, "密码错误"),
    USER_NOT_EXIST(404, "用户不存在"),

    SPEC_PARAM_NOT_FOUND(204, "规格参数查询失败"),
    UPDATE_SPEC_PARAM_FAILED(500, "商品规格参数更新失败"),
    DELETE_SPEC_PARAM_FAILED(500, "商品规格参数删除失败"),
    SPEC_PARAM_CREATE_FAILED(500, "新增规格参数失败"),
    USER_NOT_LOGIN(401, "用户未登录，请登录"),


    SPEC_GROUP_CREATE_FAILED(500, "新增规格组失败"),
    SPEC_GROUP_NOT_FOUND(204, "规格组查询失败"),
    DELETE_SPEC_GROUP_FAILED(500, "商品规格组删除失败"),
    UPDATE_SPEC_GROUP_FAILED(500, "商品规格组更新失败"),
    GENER_TOKEN_FAILED(500, "token生成失败"),


    CART_NOT_FOUND(500, "购物车数据异常"),

    CREATE_ORDER_FAILED(500, "订单创建失败"),
    STOCK_NO_ENOUGH(500, "库存不足异常"),
    ORDER_STATUE_FAILED(500, "订单状态异常"),
    ORDER_DETAILS_FAILED(500, "订单详情异常"),
    ORDER_STATUS_ERROR(500, "订单状态不正确"),
    ORDER_PARAM_ERROR(500,"订单参数有误"),
    INVALID_SIGN_ERROR(500, "无效的签名"),
    WX_PAY_ORDER_FAILED(500, "微信下单失败"),
    UPDATE_ORDER_STATUE_ERROR(500,"更新账单状态失败")
    ;
    int value;
    String message;


    public int getCode() {
        return this.value;
    }

    public String getMsg() {
        return this.message;
    }
}
