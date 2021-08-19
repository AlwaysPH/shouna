package com.qcws.shouna.dto.api;

import lombok.Data;

@Data
public class WxPayDto {
    private String orderNo;
    private String code;
    //以下为内部传递参数
    private String appId;
    private String apiKey;
    private String appSecret;
    private String merId;

    //服务器地址
    private String serverUrl;
}
