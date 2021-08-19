package com.qcws.shouna.dto.api;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WxPayResDto {
    private String code;
    private String msg;
    private String timeStamp;//时间戳从1970年1月1日00:00:00至今的秒数,即当前的时间
    private String nonceStr;//随机字符串，长度为32个字符以下。
    private String packages;//统一下单接口返回的 prepay_id 参数值，提交格式如：prepay_id=*
    private String signType;//签名类型，默认为MD5，支持HMAC-SHA256和MD5。注意此处需与统一下单的签名类型一致
    private String paySign;//签名,具体签名方案参见微信公众号支付帮助文档;
    private BigDecimal amount;//返回展示用
    //下单再次再次支付使用
    private String orderNo;
}
