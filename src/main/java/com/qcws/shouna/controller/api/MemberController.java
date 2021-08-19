package com.qcws.shouna.controller.api;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.ext.cors.EnableCORS;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.qcws.shouna.common.resp.CommissionResp;
import com.qcws.shouna.common.resp.CustomerInfoResp;
import com.qcws.shouna.common.resp.LevelResp;
import com.qcws.shouna.common.resp.TodayLevelResp;
import com.qcws.shouna.config.WxConfig;
import com.qcws.shouna.model.CustomerBill;
import com.qcws.shouna.model.CustomerCommission;
import com.qcws.shouna.model.CustomerInfo;
import com.qcws.shouna.model.CustomerLevel;
import com.qcws.shouna.service.CustomerBillService;
import com.qcws.shouna.service.CustomerCommissionService;
import com.qcws.shouna.service.CustomerInfoService;
import com.qcws.shouna.service.CustomerLevelService;
import com.qcws.shouna.utils.Constant;
import com.qcws.shouna.utils.wx.WxUtil;

import io.jboot.app.config.JbootConfigManager;
import io.jboot.db.model.Columns;
import io.jboot.support.swagger.ParamType;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 用户中心
 */
 
@RequestMapping("/api/memberinfo")
@EnableCORS
@Api(tags = " 用户中心")
public class MemberController extends ApiController {

	@Inject
	private CustomerInfoService customerInfoService;
	
	@Inject
	private CustomerBillService customerBillService;
	
	@Inject
	private CustomerCommissionService customerCommissionService;
	
	@Inject
	private CustomerLevelService customerLevelService;
  
	@ApiOperation(value = "推广二维码", httpMethod = "GET")
	public void  promotingCodes() {
	    Integer id = getLoginCustomer().getId() ;
	    Integer url = getParaToInt("url");
	    System.out.println("---url--->"+url);
	    System.out.println("---sid--->"+ getParaToInt("sid"));
	    StringBuilder scene = new StringBuilder("?id=");
	    scene.append(id); 
	    if(StrUtil.isNotBlank(url)) {
	    	scene.append("&sid=");
	    	scene.append(url);
	    } 
		try { 
			System.out.println("------>"+scene.toString());
			WxConfig config = JbootConfigManager.me().get(WxConfig.class);
			renderRet(WxUtil.downloadMiniCode(config.getAppId(),config.getSecret(),id ,scene.toString()));
		} catch (Exception e) {
			renderJson(Ret.fail(Constant.RETMSG,"请稍后再试"));
		}
	}
	
	@ApiOperation(value = "修改上级", httpMethod = "get")
	@ApiImplicitParams({
	@ApiImplicitParam(name = "inviteCode", value = "上级id", required = true, paramType = ParamType.QUERY)})
	public void  setpid() {
	    CustomerInfo customer = customerInfoService.findById(getLoginCustomer().getId());
	    Integer inviteCode = getParaToInt("inviteCode");
	    if(inviteCode==null){
			renderJson(Ret.fail(Constant.RETMSG, "inviteCode"));
			return;
		}
	    if(customer.getPid()==0 || null == customer.getPid()) {
	    	customer.setPid(inviteCode);
	    	customer.saveOrUpdate();
	    }
	    renderJson(Ret.ok());
	}
 
	@ApiOperation(value = "钱包", httpMethod = "GET")
	public void  wallet() {
		CustomerInfo customer = customerInfoService.findById(getLoginCustomer().getId()); 
		CustomerInfoResp resp = new CustomerInfoResp();
		resp.setBalance(customer.getBalance());
		resp.setId(customer.getId());
		resp.setNickname(customer.getNickname());
		resp.setUsername(customer.getUsername());
		BigDecimal sum = Db.queryBigDecimal("select COALESCE(sum(amount),0)  from customer_cashout where customer_id = ?",customer.getId());
		resp.setTotalCashout(sum);
		renderJson(Ret.ok("data",resp));
	}
	
	@ApiOperation(value = "佣金", httpMethod = "POST")
	public void commission() {
		Integer uid = getLoginCustomer().getId();
		CommissionResp resp = new CommissionResp();
		List<CustomerLevel> list = customerLevelService.findAll();
		List<LevelResp> levellist = new ArrayList<LevelResp>();
		List<TodayLevelResp> todayLevellist = new ArrayList<TodayLevelResp>();
		for(CustomerLevel level:list) {
			LevelResp levelResp = new LevelResp();
			levelResp.setId(level.getId());
			levelResp.setLevel(level.getLevel());
			levelResp.setNum(Db.queryInt("select count(*) from customer_info where pid = ? and level_id = ?",uid,level.getId()));
			levelResp.setOrderRate(level.getOrderRate());
			levelResp.setTitle(level.getTitle());
			levellist.add(levelResp);
			
			TodayLevelResp todayLevelResp = new TodayLevelResp();
			todayLevelResp.setId(level.getId());
			todayLevelResp.setLevel(level.getLevel());
			todayLevelResp.setNum(Db.queryInt("select count(*) from customer_info where pid = ? and level_id = ? and to_days(regtime) = to_days(now())",uid,level.getId()));
			todayLevelResp.setTitle(level.getTitle());
			todayLevellist.add(todayLevelResp);
		}
		resp.setPopulation(Db.queryInt("select count(*) from customer_info where pid = ?",uid));
		resp.setCommission(Db.queryBigDecimal("SELECT sum(amount) FROM `customer_commission` where customer_id = ?",uid)); 
		resp.setLevellist(levellist);
		resp.setTodayLevellist(todayLevellist);
		renderJson(resp);
	}
	
	@ApiOperation(value = "佣金列表", httpMethod = "GET") 
	@ApiImplicitParams({ 
         @ApiImplicitParam(name = "page", value = "页码数", required = true, paramType = ParamType.QUERY),
         @ApiImplicitParam(name = "limit", value = "每页记录数", required = true, paramType = ParamType.QUERY),
         @ApiImplicitParam(name = "level", value = "等级", required = false, paramType = ParamType.QUERY),
    })
	public void commissionList() {
		    int page = getParaToInt("page") == null ? 1: getParaToInt("page");
	        int limit = getParaToInt("limit") == null ? 10:getParaToInt("limit");
	        String level = getPara("level");
	        Columns columns = Columns.create("customer_id", getLoginCustomer().getId()); 
	        if (StrUtil.isNotEmpty(level)) {
	            columns.eq("level", level);
	        }
	        Page<CustomerCommission> customerCommissionPage = customerCommissionService.paginateByColumns(page, limit, columns, "id desc");
	        renderJson(Ret.ok("data", customerCommissionPage));
	}
	
	
	@ApiOperation(value = "佣金列表", httpMethod = "GET") 
	@ApiImplicitParams({ 
         @ApiImplicitParam(name = "page", value = "页码数", required = true, paramType = ParamType.QUERY),
         @ApiImplicitParam(name = "limit", value = "每页记录数", required = true, paramType = ParamType.QUERY),
         @ApiImplicitParam(name = "level", value = "等级", required = false, paramType = ParamType.QUERY),
    })
	public void commissionTodyList() { 
		    int page = getParaToInt("page") == null ? 1: getParaToInt("page");
	        int limit = getParaToInt("limit") == null ? 10:getParaToInt("limit");
	        String level = getPara("level");
	        Columns columns = Columns.create("customer_id",getLoginCustomer().getId()).gt("commission_time", getZero());
	        if (StrUtil.isNotEmpty(level)) {
	            columns.eq("level", level);
	        } 
	        Page<CustomerCommission> customerCommissionPage = customerCommissionService.paginateByColumns(page, limit, columns, "id desc");
	        renderJson(Ret.ok("data", customerCommissionPage));
	}
 
	 @ApiOperation(value = "钱包明细", httpMethod = "GET")
	    @ApiImplicitParams({
	            @ApiImplicitParam(name = "page", value = "页码数", required = true, paramType = ParamType.QUERY),
	            @ApiImplicitParam(name = "limit", value = "每页记录数", required = true, paramType = ParamType.QUERY)
	    })
	    public void walletlist() { 
	        int page = getParaToInt("page") == null ? 1: getParaToInt("page");
	        int limit = getParaToInt("limit") == null ? 10:getParaToInt("limit");
	        Columns columns = Columns.create("customer_id", getLoginCustomer().getId());
	        Page<CustomerBill> billPage = customerBillService.paginateByColumns(page, limit, columns, "id desc");
	        renderJson(Ret.ok("data", billPage));
	    }
	 
	 
	 private Date getZero() {
		 LocalDate localDate = LocalDate.now();
		 ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
	     return Date.from(zonedDateTime.toInstant());
	 }
}
