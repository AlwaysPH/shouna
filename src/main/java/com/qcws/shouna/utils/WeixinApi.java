package com.qcws.shouna.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.hutool.http.HttpUtil;
import com.qcws.shouna.config.WxConfig;
import io.jboot.app.config.JbootConfigManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WeixinApi {

	public static String getOpenId(String code) {
		WxConfig config = JbootConfigManager.me().get(WxConfig.class);
		log.info("---------> code:" + code);
		String url = "https://api.weixin.qq.com/sns/jscode2session";
		url += "?appid="+config.getAppId();//自己的appid
		url += "&secret="+config.getSecret();//自己的appSecret
		url += "&js_code=" + code;
		url += "&grant_type=authorization_code";
		url += "&connect_redirect=1";
		// 改用hutool工具包
		String res = HttpUtil.get(url, 5000);
		log.info("---------> result:" + res);
		JSONObject jsonObject = JSON.parseObject(res);
		log.info("---------> jsonObject:" + jsonObject);
		String openid = jsonObject.getString("openid");
		return openid;
	}

}
