package com.qcws.shouna.config;

import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Routes;
import com.jfinal.template.Engine;
import com.qcws.shouna.interceptors.ApiAuthInterceptor;
import com.qcws.shouna.shiro.ShiroUtils;

import cn.hutool.core.util.StrUtil;
import io.jboot.core.listener.JbootAppListenerBase;
import io.jboot.web.cors.CORSInterceptor;

public class AppConfigListener extends JbootAppListenerBase {

	@Override
	public void onInterceptorConfig(Interceptors interceptors) {
		interceptors.addGlobalActionInterceptor(new CORSInterceptor());
		interceptors.addGlobalActionInterceptor(new ApiAuthInterceptor());
	}
	@Override
	public void onConstantConfig(Constants constants) {
		constants.setDevMode(false);
	}
	
	@Override
	public void onRouteConfig(Routes routes) {
		routes.setBaseViewPath("WEB-INF/views/");
	}
	
	@Override
	public void onEngineConfig(Engine engine) {
		Engine.setFastMode(true);
		engine.setDevMode(true);
		engine.addSharedMethod(StrUtil.class);
		engine.addSharedMethod(ShiroUtils.class);
		engine.addSharedFunction("WEB-INF/views/admin/lib/_layout.html");
	}
	
}
