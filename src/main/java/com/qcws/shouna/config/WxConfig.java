package com.qcws.shouna.config;

import io.jboot.app.config.annotation.ConfigModel;
import lombok.Data;

@Data
@ConfigModel(prefix = "wx.merchant")
public class WxConfig {
    private String appId;
    private String secret;
    private String apiKey;
    private String merchantId;
    //服务器地址
    private String serverUrl;
}
