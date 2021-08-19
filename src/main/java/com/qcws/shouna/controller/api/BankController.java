package com.qcws.shouna.controller.api;

import com.hazelcast.util.StringUtil;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.qcws.shouna.model.CustomerBank;
import com.qcws.shouna.model.SysBankInfo;
import com.qcws.shouna.service.CustomerBankService;
import com.qcws.shouna.service.SysBankInfoService;
import com.qcws.shouna.utils.Constant;
import io.jboot.db.model.Columns;
import io.jboot.support.swagger.ParamType;
import io.jboot.web.controller.annotation.RequestMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * 银行卡信息
 */
@RequestMapping("/api/bank")
@Api(tags = "银行卡信息")
public class BankController extends ApiController {

    @Inject private CustomerBankService customerBankService;
    @Inject private SysBankInfoService bankInfoService;
 
    @ApiOperation(value = "银行卡列表", httpMethod = "GET")
    public void list() {
        List<CustomerBank> items = customerBankService.findListByColumns(Columns.create("customer_id", getLoginCustomer().getId()));
        renderJson(Ret.ok("items", items));
    }

    @ApiOperation(value = "加载银行卡信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "银行卡id", required = true, paramType = ParamType.QUERY)
    })
    public void load() {
        CustomerBank bank = customerBankService.findById(getParaToInt("id"));
        if (bank == null || !bank.getCustomerId().equals(getLoginCustomer().getId())) {
            renderJson(Ret.fail(Constant.RETMSG, "参数错误"));
            return;
        }
        renderJson(Ret.ok("data", bank));
    }

    @ApiOperation(value = "删除银行卡", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "银行卡id", required = true, paramType = ParamType.QUERY)
    })
    public void remove() {
        Db.update("delete from customer_bank where id = ? and customer_id = ?", getParaToInt("id"), getLoginCustomer().getId());
        renderJson(Ret.ok());
    }

    @ApiOperation(value = "添加银行卡", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "accountName", value = "账户姓名", required = true, paramType = ParamType.FORM),
            @ApiImplicitParam(name = "cardNo", value = "银行卡号", required = true, paramType = ParamType.FORM),
            @ApiImplicitParam(name = "bankName", value = "银行名称", required = true, paramType = ParamType.FORM),
            @ApiImplicitParam(name = "branch", value = "开户行", required = true, paramType = ParamType.FORM)
    })
    public void add() {
        String accountName = getParaByJson("accountName");
        String cardNo = getParaByJson("cardNo");
      //  String telephone = getParaByJson("telephone");
      //  String isDefault = getParaByJson("isDefault");
        String bankName = getParaByJson("bankName");
      //  String bankAddress = getParaByJson("bankAddress");
      //  String identity = getParaByJson("identity");
        String branch = getParaByJson("branch");
      //  String accountType = getParaByJson("accountType");


        if(StringUtil.isNullOrEmpty(accountName)){
            renderJson(Ret.fail(Constant.RETMSG, "账户姓名不能为空"));
            return;
        }
        if(StringUtil.isNullOrEmpty(cardNo)){
            renderJson(Ret.fail(Constant.RETMSG, "银行卡号不能为空"));
            return;
        }
//        if(StringUtil.isNullOrEmpty(telephone)){
//            renderJson(Ret.fail(Constant.RETMSG, "手机号码不能为空"));
//            return;
//        }
//        if(StringUtil.isNullOrEmpty(isDefault)){
//            renderJson(Ret.fail(Constant.RETMSG, "是否默认银行卡不能为空"));
//            return;
//        }
        if(StringUtil.isNullOrEmpty(bankName)){
            renderJson(Ret.fail(Constant.RETMSG, "银行名称不能为空"));
            return;
        }
//        if(StringUtil.isNullOrEmpty(bankAddress)){
//            renderJson(Ret.fail(Constant.RETMSG, "银行地址不能为空"));
//            return;
//        }
//        if(StringUtil.isNullOrEmpty(identity)){
//            renderJson(Ret.fail(Constant.RETMSG, "身份证号码不能为空"));
//            return;
//        }
        if(StringUtil.isNullOrEmpty(branch)){
            renderJson(Ret.fail(Constant.RETMSG, "开户行不能为空"));
            return;
        }
//        if(StringUtil.isNullOrEmpty(accountType)){
//            renderJson(Ret.fail(Constant.RETMSG, "账户类型不能为空"));
//            return;
//        }

        CustomerBank bank = new CustomerBank();
        bank.setAccountName(accountName);
        bank.setCardNo(cardNo);
       // bank.setTelphone(telephone);
       // bank.setIsDefault(isDefault);
        bank.setBankName(bankName);
       // bank.setBankAddress(bankAddress);
       // bank.setIdentity(identity);
        bank.setBranch(branch);
        bank.setCustomerId(getLoginCustomer().getId());
        bank.save();
        renderJson(Ret.ok());
    }

    @ApiOperation(value = "编辑地址", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "accountName", value = "账户姓名", required = true, paramType = ParamType.FORM),
            @ApiImplicitParam(name = "cardNo", value = "银行卡号", required = true, paramType = ParamType.FORM),
            @ApiImplicitParam(name = "telephone", value = "电话号码", required = true, paramType = ParamType.FORM),
            @ApiImplicitParam(name = "isDefault", value = "是否默认,Y或N", required = true, paramType = ParamType.FORM),
            @ApiImplicitParam(name = "bankName", value = "银行名称", required = true, paramType = ParamType.FORM),
            @ApiImplicitParam(name = "bankAddress", value = "银行地址", required = true, paramType = ParamType.FORM),
            @ApiImplicitParam(name = "identity", value = "身份证", required = true, paramType = ParamType.FORM),
            @ApiImplicitParam(name = "branch", value = "分行", required = true, paramType = ParamType.FORM),
            @ApiImplicitParam(name = "accountType", value = "账户类型 1信用账户 2储蓄账户", required = true, paramType = ParamType.FORM),
            @ApiImplicitParam(name = "id", value = "银行id", required = true, paramType = ParamType.FORM)
    })
    public void edit() {
        String accountName = getParaByJson("accountName");
        String cardNo = getParaByJson("cardNo");
        String telephone = getParaByJson("telephone");
        String isDefault = getParaByJson("isDefault");
        String bankName = getParaByJson("bankName");
        String bankAddress = getParaByJson("bankAddress");
        String identity = getParaByJson("identity");
        String branch = getParaByJson("branch");
        String accountType = getParaByJson("accountType");
        String id = getParaByJson("id");

        if(StringUtil.isNullOrEmpty(id)){
            renderJson(Ret.fail(Constant.RETMSG, "id不能为空"));
            return;
        }
        if(StringUtil.isNullOrEmpty(accountName)){
            renderJson(Ret.fail(Constant.RETMSG, "账户姓名不能为空"));
            return;
        }
        if(StringUtil.isNullOrEmpty(cardNo)){
            renderJson(Ret.fail(Constant.RETMSG, "银行卡号不能为空"));
            return;
        }
        if(StringUtil.isNullOrEmpty(telephone)){
            renderJson(Ret.fail(Constant.RETMSG, "手机号码不能为空"));
            return;
        }
        if(StringUtil.isNullOrEmpty(isDefault)){
            renderJson(Ret.fail(Constant.RETMSG, "是否默认银行卡不能为空"));
            return;
        }
        if(StringUtil.isNullOrEmpty(bankName)){
            renderJson(Ret.fail(Constant.RETMSG, "银行名称不能为空"));
            return;
        }
        if(StringUtil.isNullOrEmpty(bankAddress)){
            renderJson(Ret.fail(Constant.RETMSG, "银行地址不能为空"));
            return;
        }
        if(StringUtil.isNullOrEmpty(identity)){
            renderJson(Ret.fail(Constant.RETMSG, "身份证号码不能为空"));
            return;
        }
        if(StringUtil.isNullOrEmpty(branch)){
            renderJson(Ret.fail(Constant.RETMSG, "分行不能为空"));
            return;
        }
        if(StringUtil.isNullOrEmpty(accountType)){
            renderJson(Ret.fail(Constant.RETMSG, "账户类型不能为空"));
            return;
        }

        CustomerBank bank = new CustomerBank();
        bank.setAccountName(accountName);
        bank.setCardNo(cardNo);
        bank.setTelphone(telephone);
        bank.setIsDefault(isDefault);
        bank.setBankName(bankName);
        bank.setBankAddress(bankAddress);
        bank.setIdentity(identity);
        bank.setBranch(branch);
        bank.setCustomerId(getLoginCustomer().getId());
        bank.setId(Integer.parseInt(id));

        CustomerBank sqlBank = customerBankService.findById(bank.getId());

        if (bank == null || sqlBank == null || !sqlBank.getCustomerId().equals(getLoginCustomer().getId())) {
            renderJson(Ret.fail(Constant.RETMSG, "参数错误"));
            return;
        }
        bank.update();
        renderJson(Ret.ok());
    }
    
    
    @ApiOperation(value = "银行列表", httpMethod = "GET") 
    public void banklist() {
        List<SysBankInfo> banklist = bankInfoService.findAll();
        renderJson(Ret.ok("banklist", banklist));
    }
}
