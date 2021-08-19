package com.qcws.shouna.config;

import io.jboot.app.config.annotation.ConfigModel;
import lombok.Data;

@Data
@ConfigModel(prefix = "aliyun.oss")
public class OSSConfig {
	private String endpoint;
	private String accessKeyId;
	private String accessKeySecret;
	private String bucketname;
}
