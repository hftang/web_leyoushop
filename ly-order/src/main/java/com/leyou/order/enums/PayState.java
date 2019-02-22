package com.leyou.order.enums;

/**
 * @author hftang
 * @date 2019-02-22 16:17
 * @desc 支付状态
 */
public enum PayState {

    NOT_PAY(0),
    SUCCESS(1),
    FAIL(2);


    PayState(int value) {
        this.value = value;
    }

    int value;

    public Integer getValue() {
        return value;
    }

}
