package com.qcws.shouna.config;

import com.jfinal.server.undertow.WebBuilder;

import io.jboot.app.JbootWebBuilderConfiger;

public class WebConfig implements JbootWebBuilderConfiger {

	@Override
	public void onConfig(WebBuilder web) {
		// durid监控后台servler
		web.addServlet("DruidStatView", "com.alibaba.druid.support.http.StatViewServlet");
		web.addServletMapping("DruidStatView", "/druid/*");
		// durid统计监听器
		web.addFilter("DruidWebStatFilter", "com.alibaba.druid.support.http.WebStatFilter");
		web.addFilterInitParam("DruidWebStatFilter", "exclusions", "/druid/*,/assets/*");
		web.addFilterUrlMapping("DruidWebStatFilter", "/*");
	}

}
