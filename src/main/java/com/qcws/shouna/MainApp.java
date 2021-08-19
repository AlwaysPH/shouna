package com.qcws.shouna;

import com.qcws.shouna.config.WebConfig;

import io.jboot.Jboot;
import io.jboot.app.JbootApplication;

public class MainApp {
	public static void main(String[] args) {
		if (Jboot.isDevMode()) {
			JbootApplication.setBootArg("jboot.swagger.path", "/document");
			JbootApplication.setBootArg("jboot.swagger.title", "APi接口说明文档V1.0");
			JbootApplication.setBootArg("jboot.swagger.description", "用户、商品、订单等api接口。header中请加入token");
			JbootApplication.setBootArg("jboot.swagger.version", "1.0");
			JbootApplication.setBootArg("jboot.swagger.host", "127.0.0.1:8080");
		}
		JbootApplication.run(args, new WebConfig());
	}

}
