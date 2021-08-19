package com.qcws.shouna.controller.api;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.qcws.shouna.common.resp.RecordResp;
import com.qcws.shouna.model.CustomerBank;
import com.qcws.shouna.model.CustomerBill;
import com.qcws.shouna.model.CustomerCashout;
import com.qcws.shouna.model.CustomerInfo;
import com.qcws.shouna.service.CustomerBankService;
import com.qcws.shouna.service.CustomerCashoutService;
import com.qcws.shouna.service.CustomerInfoService;
import com.qcws.shouna.utils.Constant;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import io.jboot.db.model.Columns;
import io.jboot.support.swagger.ParamType;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 提现管理
 */
@RequestMapping("/api/cashout")
@Api(tags = "提现管理")
public class MemberCashoutController extends ApiController {

	@Inject private CustomerCashoutService customerCashoutService;
    @Inject private CustomerInfoService customerInfoService;
    @Inject private CustomerBankService customerBankService;

    @ApiOperation(value = "提交提现订单", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "amount", value = "提现金额", required = true, paramType = ParamType.FORM),
            @ApiImplicitParam(name = "bankId", value = "银行卡id", required = true, paramType = ParamType.FORM),
            @ApiImplicitParam(name = "remark", value = "备注信息", required = true, paramType = ParamType.FORM),
    })
    @Before(Tx.class)
    public void create() {
        String amountStr = getParaByJson("amount");
        BigDecimal amount = null;
        try { amount = new BigDecimal(amountStr); }
        catch (Exception e) {
            renderJson(Ret.fail(Constant.RETMSG, "金额错误"));
            return;
        }

        CustomerInfo customer = customerInfoService.findById(getLoginCustomer().getId());
//		CustomerBank bank = customerBankService.findById(getParaByJson("bankId"));
//		if (bank == null || !bank.getCustomerId().equals(customer.getId())) {
//            renderJson(Ret.fail(Constant.RETMSG, "银行卡id错误"));
//			return;
//		}

        if (customer.getBalance().compareTo(amount) == -1) {
            renderJson(Ret.fail(Constant.RETMSG, "余额不足"));
            return;
        }
        if(customer.getVersion()==null) {
        	customer.setVersion(1);
        	customer.setBalance(customer.getBalance().subtract(amount));
        	customer.update();
        }else {
        	int row = Db.update("update customer_info set balance = balance - ?, version = version + 1 where id = ? and version = ?", amount, customer.getId(), customer.getVersion());
        	if (row < 1) {
                renderJson(Ret.fail(Constant.RETMSG, "系统错误,请稍后再试"));
                return;
            } 
        } 
        String orderNo = DateUtil.format(new Date(), "yyMMddHHmmssSSS") + RandomUtil.randomNumbers(8);
        CustomerCashout cashout = new CustomerCashout();
        cashout.setOrderNo(orderNo);
        cashout.setCustomerId(customer.getId());
//        cashout.setAccountName(bank.getAccountName());
//        cashout.setBankName(bank.getBankName());
//        cashout.setCardNo(bank.getCardNo());
//        cashout.setBankAddress(bank.getBankAddress());
        cashout.setAmount(amount);
        cashout.setStatus(Constant.WAIT);
        cashout.setRemark(getParaByJson("remark"));
        cashout.setAddtime(new Date());
        cashout.save();

        CustomerBill bill = new CustomerBill();
        bill.setCustomerId(customer.getId());
        bill.setType(Constant.CASHOUT);
        bill.setOrderNo(orderNo);
        bill.setContent("提现申请" +amount);
        bill.setAmount(BigDecimal.ZERO.subtract(amount).setScale(2, BigDecimal.ROUND_HALF_UP));
        bill.setBalance(customer.getBalance().subtract(amount).setScale(2, BigDecimal.ROUND_HALF_UP));
        bill.setAddtime(new Date());
        bill.setStatus("0");
        bill.save();
        renderJson(Ret.ok());
    }

    
    
    @ApiOperation(value = "提现信息", httpMethod = "GET") 
    public void info() {
    	RecordResp resp = new RecordResp();
    	List<Record> items = Db.find("SELECT b.id, b.bank_name,i.bank_img,RIGHT(b.card_no,4) as card_no  FROM `customer_bank` b INNER JOIN sys_bank_info i ON b.bank_name=i.bank_name where b.customer_id=?",getLoginCustomer().getId());  
    	Record record = Db.findFirst("SELECT i.id, i.balance, i.balance - (SELECT sum( amount ) FROM customer_cashout WHERE customer_id = i.id AND STATUS = 'wait' ) AS cancarry FROM `customer_info` i  WHERE i.id = ?",getLoginCustomer().getId());
    	resp.setItems(items);
    	resp.setRecord(record);
    	renderJson(Ret.ok("resp", resp));
    }

    @ApiOperation(value = "提现记录", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码数", required = true, paramType = ParamType.QUERY),
            @ApiImplicitParam(name = "limit", value = "每页记录数", required = true, paramType = ParamType.QUERY),
            @ApiImplicitParam(name = "status", value = "状态", required = false, paramType = ParamType.QUERY),
    })
    public void list() {
        int page = getParaToInt("page") == null ? 1: getParaToInt("page");
        int limit = getParaToInt("limit") == null ? 10:getParaToInt("limit");
        String status = getPara("status");
        
        Columns columns = Columns.create("customer_id", getLoginCustomer().getId());
        if (StrUtil.isNotEmpty(status)) {
            columns.eq("status", status);
        }
        Page<CustomerCashout> cashoutPage = customerCashoutService.paginateByColumns(page, limit, columns, "id desc");
        renderJson(Ret.ok("data", cashoutPage));
    }

}
