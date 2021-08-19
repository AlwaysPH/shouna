package com.qcws.shouna.controller.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.NotAction;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Ret;
import com.qcws.shouna.config.R;
import com.qcws.shouna.model.CustomerInfo;

import io.jboot.web.controller.JbootController;

public class ApiController extends JbootController {

	private CustomerInfo loginCustomer;
	private JSONObject reqJson;

	@NotAction
	public String getParaByJson(String key) {
		if (reqJson == null) {
			reqJson = JSON.parseObject(HttpKit.readData(getRequest()));
		}
		return reqJson.getString(key);
	}

	@NotAction
	public void renderRet(Object data) {
		renderJson(Ret.ok("data", data));
	}
	
	@NotAction
	public void errorRet(String msg) {
		renderJson(Ret.fail(R.MSG, msg));
	}
	
	@NotAction
	public void setLoginCustomer(CustomerInfo loginCustomer) {
		this.loginCustomer = loginCustomer;
	}
	
	@NotAction
	public CustomerInfo getLoginCustomer() {
		return loginCustomer;
	}
	
}
