package com.qcws.shouna.controller.api;


import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qcws.shouna.service.CustomerRechargeService;
import com.qcws.shouna.utils.Constant;

import io.jboot.web.controller.annotation.RequestMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 充值信息
 */
@RequestMapping("/api/recharge")
@Api(tags = "充值信息")
public class MemberRechargeController extends ApiController {

	@Inject private CustomerRechargeService customerRechargeService; 
	
	@ApiOperation(value = "充值列表", httpMethod = "POST") 
	public void list() { 
		List<Record> items = Db.find("select id, order_no, amount, status, addtime, paytime from customer_recharge where status = ? and customer_id = ? order by id desc", Constant.FINISH, getLoginCustomer().getId());
		renderJson(Ret.ok("items", items));
	}
}
