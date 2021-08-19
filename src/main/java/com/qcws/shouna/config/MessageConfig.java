package com.qcws.shouna.config;

import io.jboot.app.config.annotation.ConfigModel;
import lombok.Data;

@Data
@ConfigModel(prefix = "aliyun.message")
public class MessageConfig {

    private String accessKeyId;

    private String accessKeySecret;

    private String templateCode;

    private String url;
}
