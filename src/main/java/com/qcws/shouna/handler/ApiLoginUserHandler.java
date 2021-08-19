package com.qcws.shouna.handler;

import cn.hutool.core.util.RandomUtil;
import com.jfinal.core.JFinal;
import com.qcws.shouna.model.CustomerInfo;

/**
 * 登录用户控制器
 * 
 * @author 咸鱼
 */
public class ApiLoginUserHandler {
	public static final String LOGIN_KEY = "token";
	public static final String CURRENTUSER = "LOGIN_CUSTOMER";
	
	public static CustomerInfo currentCustomer() {
		return (CustomerInfo) JFinal.me().getServletContext().getAttribute(CURRENTUSER);
	}
	
	public static int getCustomerId(String token) {
		return Integer.parseInt(token.substring(0, token.indexOf("_")));
	}
	
	public static long getCreated(String token) {
		return Long.parseLong(token.substring(token.indexOf("@") + 1));
	}
	
	public static String createToken(CustomerInfo info) {
		return info.getId() + "_" + RandomUtil.randomString(100) + "@" + System.currentTimeMillis();
	}

}
