package com.qcws.shouna.controller.api;


import com.hazelcast.util.StringUtil;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.qcws.shouna.model.*;
import com.qcws.shouna.service.*;
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
 * 地址信息
 */
@RequestMapping("/api/address")
@Api(tags = "地址信息")
public class AddressController extends ApiController {

	@Inject private CustomerAddressService customerAddressService;
	@Inject private SysCityService cityService;

   
	 @ApiOperation(value = "城市列表", httpMethod = "GET")
	    public void citylist() {
	        List<SysCity> list = cityService.findAll();
	        renderJson(Ret.ok("list", list));
	    }
	
	
	
	@ApiOperation(value = "地址列表", httpMethod = "GET")
    public void list() {
        List<CustomerAddress> addresses = customerAddressService.findListByColumns(Columns.create("customer_id", getLoginCustomer().getId()), "id desc");
        renderJson(Ret.ok("items", addresses));
    }
    
    
    @ApiOperation(value = "最近的地址", httpMethod = "GET")
    public void info() {
         CustomerAddress  address  = customerAddressService.findFirstByColumns(Columns.create("customer_id", getLoginCustomer().getId()), "id desc");
        renderJson(Ret.ok("items", address));
    }
    

    @ApiOperation(value = "加载地址信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "地址id", required = true, paramType = ParamType.QUERY),
    })
    public void load() {
        CustomerAddress address = customerAddressService.findById(getParaToInt("id"));
        if (address == null || !address.getCustomerId().equals(getLoginCustomer().getId())) {
            renderJson(Ret.fail(Constant.RETMSG, "参数错误"));
            return;
        }
        renderJson(Ret.ok("data", address));
    }

    @ApiOperation(value = "添加地址", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "realname", value = "联系人姓名", required = true, paramType = ParamType.FORM),
            @ApiImplicitParam(name = "telphone", value = "手机号码", required = true, paramType = ParamType.FORM),
            @ApiImplicitParam(name = "address", value = "详细地址", required = true, paramType = ParamType.FORM),
            @ApiImplicitParam(name = "city", value = "城市", required = true, paramType = ParamType.FORM)
    })
    public void add() {
        String realname = getParaByJson("realname");
        String telphone = getParaByJson("telphone");
        String address = getParaByJson("address");
        String city = getParaByJson("city");

        if(StringUtil.isNullOrEmpty(realname)){
            renderJson(Ret.fail(Constant.RETMSG, "真实姓名不能为空"));
            return;
        }
        if(StringUtil.isNullOrEmpty(telphone)){
            renderJson(Ret.fail(Constant.RETMSG, "手机号码不能为空"));
            return;
        }
        if(StringUtil.isNullOrEmpty(address)){
            renderJson(Ret.fail(Constant.RETMSG, "地址不能为空"));
            return;
        }
        if(StringUtil.isNullOrEmpty(city)){
            renderJson(Ret.fail(Constant.RETMSG, "城市不能为空"));
            return;
        }
        CustomerAddress customerAddress = new CustomerAddress();
        customerAddress.setRealname(realname);
        customerAddress.setTelphone(telphone);
        customerAddress.setAddress(address);
        customerAddress.setCity(city);
        customerAddress.setCustomerId(getLoginCustomer().getId());
        customerAddress.save();
        renderRet(customerAddress.getId());
    }

    @ApiOperation(value = "删除地址", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "地址id", required = true, paramType = ParamType.QUERY)
    })
    public void remove() {
        Db.update("delete from customer_address where id = ? and customer_id = ?", getParaToInt("id"), getLoginCustomer().getId());
        renderJson(Ret.ok());
    }

    @ApiOperation(value = "添加修改", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "地址id", required = true, paramType = ParamType.FORM),
            @ApiImplicitParam(name = "realname", value = "联系人姓名", required = true, paramType = ParamType.FORM),
            @ApiImplicitParam(name = "telphone", value = "手机号码", required = true, paramType = ParamType.FORM),
            @ApiImplicitParam(name = "address", value = "详细地址", required = true, paramType = ParamType.FORM),
            @ApiImplicitParam(name = "city", value = "城市", required = true, paramType = ParamType.FORM)
    })
    public void edit() {
        String realname = getParaByJson("realname");
        String telphone = getParaByJson("telphone");
        String address = getParaByJson("address");
        String city = getParaByJson("city");
        String id = getParaByJson("id");

        if(StringUtil.isNullOrEmpty(id)){
            renderJson(Ret.fail(Constant.RETMSG, "id不能为空"));
            return;
        }
        if(StringUtil.isNullOrEmpty(realname)){
            renderJson(Ret.fail(Constant.RETMSG, "真实姓名不能为空"));
            return;
        }
        if(StringUtil.isNullOrEmpty(telphone)){
            renderJson(Ret.fail(Constant.RETMSG, "手机号码不能为空"));
            return;
        }
        if(StringUtil.isNullOrEmpty(address)){
            renderJson(Ret.fail(Constant.RETMSG, "地址不能为空"));
            return;
        }
      
        if(StringUtil.isNullOrEmpty(city)){
            renderJson(Ret.fail(Constant.RETMSG, "城市不能为空"));
            return;
        }

        CustomerAddress customerAddress = new CustomerAddress();
        customerAddress.setRealname(realname);
        customerAddress.setTelphone(telphone);
        customerAddress.setAddress(address);
        customerAddress.setCity(city);
        customerAddress.setCustomerId(getLoginCustomer().getId());
        customerAddress.setId(Integer.parseInt(id));

        CustomerAddress sqlAddress = customerAddressService.findById(customerAddress.getId());

        if (address == null || sqlAddress == null || !sqlAddress.getCustomerId().equals(getLoginCustomer().getId())) {
            renderJson(Ret.fail(Constant.RETMSG, "参数错误"));
            return;
        }
        customerAddress.update();
        renderJson(Ret.ok());
    }
}
