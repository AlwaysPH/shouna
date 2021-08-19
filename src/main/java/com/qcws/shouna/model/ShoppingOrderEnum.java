package com.qcws.shouna.model;

public enum  ShoppingOrderEnum {

    //待付款
    UN_PAY("UN_PAY", "0"),

    //待发货
    AWAIT_SHIP("AWAIT_SHIP","1"),

    //待收货
    AWAIT_RECEIPT("AWAIT_RECEIPT","2"),

    //已完成
    FINISH("FINISH","3"),

    //退款订单
    REFUND_ORDER("REFUND_ORDER","4"),

    //待退款
    PENDING_REFUND("PENDING_REFUND","5"),

    //退款成功
    REFUND_SUCCESS("REFUND_SUCCESS","6"),

    //退款失败
    REFUND_FAILED("REFUND_FAILED","7"),

    //已取消
    CANCEL("CANCEL","8"),

    //已支付
    PAID("PAID","1"),

    //审核通过
    AGREE("AGREE","1"),

    //审核拒绝
    REFUSE("REFUSE","0"),

    //待评价
    PENDING_EVALUATION("PENDING_EVALUATION","3"),

    //退款和售后
    REFUND_SALE("REFUND_SALE","4"),

    //全部
    ALL("ALL","5");

    private String name;

    private String value;

    ShoppingOrderEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
