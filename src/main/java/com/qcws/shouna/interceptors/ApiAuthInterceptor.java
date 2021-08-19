package com.qcws.shouna.interceptors;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.Ret;
import com.qcws.shouna.controller.api.ApiController;
import com.qcws.shouna.handler.ApiLoginUserHandler;
import com.qcws.shouna.model.CustomerInfo;
import com.qcws.shouna.utils.Constant;
import com.qcws.shouna.utils.Result;
import io.jboot.Jboot;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ApiAuthInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		if(inv.getActionKey().contains("/api")){
			log.info("api接口拦截-----> 当前路径:" + inv.getActionKey());
            Set<String> noIncludeUrls = new HashSet<String>();
            noIncludeUrls.add("/api/member/register");
            noIncludeUrls.add("/api/member/wxLogin");
            noIncludeUrls.add("/api/website/init");
            noIncludeUrls.add("/api/website/items");
            noIncludeUrls.add("/api/website/detail");
            noIncludeUrls.add("/api/item/category");
            noIncludeUrls.add("/api/item/itemList");
            noIncludeUrls.add("/api/payment/doBack");
            noIncludeUrls.add("/api/payment/doFinalBack");
            noIncludeUrls.add("/api/payment/doAdvanceBack");
            noIncludeUrls.add("/api/item/load");
            noIncludeUrls.add("/api/item/courseIndexList");
            noIncludeUrls.add("/api/item/neworder");
            noIncludeUrls.add("/api/address/citylist");
            noIncludeUrls.add("/api/mall/getMallProduct");
            noIncludeUrls.add("/api/mall/getProductDetail");
//            noIncludeUrls.add("/api/myorder/orderList");
            if (!noIncludeUrls.contains(inv.getActionKey())) {
            	ApiController controller = (ApiController) inv.getController();
                HttpServletResponse response = controller.getResponse();
                String token = inv.getController().getHeader(ApiLoginUserHandler.LOGIN_KEY);
                if (StrUtil.isEmpty(token)) {
                	log.info("token不存在！-----------------1"  );
                	unAuthorized(response);
                    return;
                }
                CustomerInfo customerInfo = null;
                try {
                	customerInfo = CustomerInfo.dao.findById(ApiLoginUserHandler.getCustomerId(token));
                    if (customerInfo == null) {
                    	log.info("用户不存在！-----------------2"  );
                    	unAuthorized(response);
                        return;
                    }
                    String redisToken = Jboot.getRedis().get(customerInfo.getTelphone() + "_token");
                    if(StrUtil.isEmpty(redisToken) || !redisToken.equals(token)){
                    	log.info("redisToken不存在！-----------------3"  );
                    	unAuthorized(response);
                        return;
                    }
                    controller.setLoginCustomer(customerInfo);
                }catch (Exception e){
                    log.info("token错误:" + e.getMessage());
                    unAuthorized(response);
                    return;
                }
            }
		}
		inv.invoke();
	}


    public boolean unAuthorized(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json;charset=utf8");
        response.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,PATCH,OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Origin,X-Requested-With,Content-Type,Accept,token");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        try {
            PrintWriter writer = response.getWriter();
            writer.write(JSON.toJSONString(Ret.fail(Constant.RETMSG, Result.UNAUTHORIZED)));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
