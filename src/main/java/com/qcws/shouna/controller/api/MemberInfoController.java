package com.qcws.shouna.controller.api;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.hazelcast.util.StringUtil;
import com.jfinal.aop.Inject;
import com.jfinal.ext.cors.EnableCORS;
import com.jfinal.kit.Ret;
import com.qcws.shouna.handler.ApiLoginUserHandler;
import com.qcws.shouna.model.CustomerInfo;
import com.qcws.shouna.service.CustomerInfoService;
import com.qcws.shouna.utils.Constant;
import com.qcws.shouna.utils.WeixinApi;

import io.jboot.Jboot;
import io.jboot.db.model.Columns;
import io.jboot.support.swagger.ParamType;
import io.jboot.web.controller.annotation.RequestMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户管理
 */
@RequestMapping("/api/member")
@EnableCORS
@Api(tags = "客户信息")
public class MemberInfoController extends ApiController {

	@Inject
	private CustomerInfoService customerInfoService;


	@ApiOperation(value = "用户注册", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "username", value = "用户名", paramType = ParamType.FORM, required = true),
			@ApiImplicitParam(name = "openid", value = "微信账户id", paramType = ParamType.FORM, required = true),
			@ApiImplicitParam(name = "telephone", value = "手机号码", paramType = ParamType.FORM, required = true),
			@ApiImplicitParam(name = "inviteCode", value = "推荐码", paramType = ParamType.FORM, required = false),
	})
	public void register() {
		String username = getParaByJson("username");
		String openid = getParaByJson("openid");
		String telphone = getParaByJson("telephone");
		String inviteCode = getParaByJson("inviteCode");

		if (StrUtil.isEmpty(username) || StrUtil.isEmpty(telphone) || StrUtil.isEmpty(openid) || StrUtil.isEmpty(inviteCode)) {
			renderJson(Ret.fail(Constant.RETMSG, "参数错误"));
			return;
		}

		// 父节点客户
		CustomerInfo parentCustomer = customerInfoService.findById(inviteCode);
				
	    if (parentCustomer == null) {
			renderJson(Ret.fail(Constant.RETMSG, "参数错误"));
			return;
		}
		if (customerInfoService.findCountByColumns(Columns.create("username", username)) > 0) {
			renderJson(Constant.RETMSG, "用户名不能为空");
			return;
		}
		if(!StringUtil.isNullOrEmpty(telphone)){
			if (customerInfoService.findCountByColumns(Columns.create("telphone", telphone)) > 0) {
				renderJson(Constant.RETMSG, "手机号码已存在");
				return;
			}
		}

		CustomerInfo customer = new CustomerInfo();
		customer.setHeadimg(RandomUtil.randomInt(1, 23) + ".jpg");
		customer.setUsername(username);
		customer.setOpenid(openid);
		customer.setNickname(username);
		customer.setTelphone(telphone);
		customer.setStatus(Constant.NORMAL);
		customer.setLevelId(3);
		customer.setLevelNum(0);
		customer.setAgentLevel(parentCustomer.getAgentLevel() + 1);
		customer.setRegip(getIPAddress());
		customer.setRegtime(new Date());
		customer.setPid(parentCustomer.getId());
		customerInfoService.save(customer);

		renderJson(Ret.ok());
	}
 
	@ApiOperation(value = "微信一键注册登录接口",httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "code",value = "微信登录code",required = true),
			@ApiImplicitParam(name = "nickName",value = "微信昵称",required = true),
			@ApiImplicitParam(name = "avatarUrl",value = "头像Url",required = true),
			@ApiImplicitParam(name = "sex",value = "性别",required = true),
			@ApiImplicitParam(name = "city",value = "城市",required = true)
	})
	public void wxLogin() {
		String code = getParaByJson("code");
		String nickName = getParaByJson("nickName");
		String avatarUrl = getParaByJson("avatarUrl");
		String sex = getParaByJson("sex");
		String city = getParaByJson("city");

		if(StringUtils.isEmpty(code)){
			renderJson(Ret.fail(Constant.RETMSG, "参数code不能为空"));
			return;
		}

		String openId = WeixinApi.getOpenId(code);
		if(StringUtils.isEmpty(openId)){
			renderJson(Ret.fail(Constant.RETMSG, "参数错误"));
			return;
		}

		//获取客户信息
		CustomerInfo customerInfo = customerInfoService.findFirstByColumns(Columns.create("openid", openId));
		CustomerInfo info =null;
		if(null == customerInfo) {
			CustomerInfo customer = new CustomerInfo();
			String randName = RandomUtil.randomStringUpper(6).toLowerCase();
			customer.setHeadimg(avatarUrl);
			customer.setUsername(randName);
			customer.setPassword(randName + "123456");
			customer.setOpenid(openId);
			customer.setLevelId(3);
			customer.setLevelNum(0);
			customer.setStatus(Constant.NORMAL);
			customer.setNickname(nickName);
			customer.setTelphone(randName);
			customer.setSex(sex);
			if(StringUtils.isEmpty(city)){
				customerInfo.setCity("Changsha");
			}else {
				customer.setCity(city);
			}
			customer.setRegip(getIPAddress());
			customer.setRegtime(new Date());
			customer.setLoginip(getIPAddress());
			customer.setLogintime(new Date());
			customer.setBalance(BigDecimal.ZERO);
			customer.setPid(0);
			customerInfoService.save(customer);
			info= customer;
		}else{
			customerInfo.setNickname(nickName);
			customerInfo.setLoginip(getIPAddress());
			customerInfo.setLogintime(new Date());
			if(StringUtils.isEmpty(customerInfo.getCity())){
				customerInfo.setCity(city);
			}
			customerInfoService.update(customerInfo);
			info= customerInfo;
		}


		Map<String, String> loginMap = new HashMap<String, String>();
		loginMap.put("id", info.getId().toString());
		loginMap.put("telphone", info.getTelphone());
 
		String token = ApiLoginUserHandler.createToken(info);
		 
        Jboot.getRedis().set(info.getTelphone() + "_token",token);
        
        
        Map<String, String> infoMap = new HashMap<String, String>();
        infoMap.put("token", token);
        infoMap.put("id", info.getId().toString());
        
		renderJson(Ret.ok("info", infoMap));
	}

	@ApiOperation(value = "客户信息",httpMethod = "GET") 
	public void memberInfo() {
		CustomerInfo info = customerInfoService.findById(Integer.parseInt(getJwtPara("id")));
		info.setPassword("");
		renderJson(Ret.ok("data", info));
	}

}
