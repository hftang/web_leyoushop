package com.leyou.order.enums;

/**
 * @author hftang
 * @date 2019-02-21 10:45
 * @desc
 */
public enum OrderStatusEnum {

    UNPAY(1, "未付款"),
    PAYED(2, "已付款,未发货"),
    DELIVERED(3, "已发货，未确认"),
    CONFIRMED(4, "已确认，未评论"),
    CLOSED(5, "已关闭"),
    RATED(6, "已评论，交易结束");

    private int code;
    private String desc;

    OrderStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int value() {
        return this.code;
    }
}