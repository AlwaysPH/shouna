  package com.qcws.shouna.controller.api;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hazelcast.util.StringUtil;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import com.qcws.shouna.config.MessageConfig;
import com.qcws.shouna.model.*;
import com.qcws.shouna.service.CustomerAddressService;
import com.qcws.shouna.service.CustomerInfoService;
import com.qcws.shouna.service.CustomerLevelService;
import com.qcws.shouna.service.CustomerOrderService;
import com.qcws.shouna.service.ItemInfoService;
import com.qcws.shouna.service.SysCityService;
import com.qcws.shouna.utils.*;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import io.jboot.app.config.JbootConfigManager;
import io.jboot.db.model.Columns;
import io.jboot.support.swagger.ParamType;
import io.jboot.web.controller.annotation.RequestMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

  /**
 * 订单管理
 */
@RequestMapping("/api/order")
@Api(tags = "订单信息")
public class MemberOrderController extends ApiController {

	@Inject private CustomerOrderService customerOrderService;
	@Inject private CustomerInfoService customerInfoService;
    @Inject private CustomerLevelService customerLevelService;
	@Inject private ItemInfoService itemInfoService;
	@Inject private CustomerAddressService customerAddressService;
	@Inject SysCityService sysCityService;

	@ApiOperation(value = "创建课程订单", httpMethod = "POST")
	@ApiImplicitParams({
		    @ApiImplicitParam(name = "id", value = "项目id", required = true, paramType = ParamType.QUERY),
			@ApiImplicitParam(name = "addressid", value = "地址id", required = true, paramType = ParamType.QUERY)})
	public void create() {
		if (getParaByJson("id") == null) {
			renderJson(Ret.fail(Constant.RETMSG, "项目id不能为空"));
			return;
		}
		Integer itemId = Integer.parseInt(getParaByJson("id"));
		Integer addressid = getParaToInt("addressid");
		CustomerInfo customer = customerInfoService.findById(getLoginCustomer().getId());
//		CustomerLevel level = customerLevelService.findById(customer.getLevelId());

		ItemInfo item = itemInfoService.findById(itemId);
		if (null == item) {
			renderJson(Ret.fail(Constant.RETMSG, "参数错误"));
			return;
		}
		if(0 < customerOrderService.findCountByColumns(Columns.create("status","finish").eq("customer_id",customer.getId()).eq("item_id",itemId))) {
			renderJson(Ret.fail(Constant.RETMSG, "请勿重复提交！"));
			return;
		}

		CustomerOrder order = new CustomerOrder();
		order.setItemId(item.getId());
		order.setCustomerId(customer.getId());
		order.setItemName(item.getName());
		order.setPrice(item.getSaleAmount());
		order.setStatus(Constant.UNDONE);
		order.setSettleStatus(Constant.UNDONE);
		order.setCommission(BigDecimal.ZERO);
//		order.setOrderRate(new BigDecimal(item.getCommissionRate()));
//		order.setOrderRate(level.getOrderRate());
		if(addressid!=null) {
			CustomerAddress address = customerAddressService.findById(addressid);
			order.setTelphone(address.getTelphone());
			order.setCity(getCity(customer.getCity(), address));
		}else {
			order.setCity(getCity(customer.getCity(), null));
		}

		order.setOrderNo(DateUtil.format(new Date(), "yyMMddHHmmssSSS") + RandomUtil.randomNumbers(6));
		order.setAddtime(new Date());
		order.save();
		renderRet(order.getOrderNo());
	}


	  @ApiOperation(value = "创建收纳订单", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "项目id", required = true, paramType = ParamType.FORM),
			@ApiImplicitParam(name = "userCount", value = "使用人数", required = true, paramType = ParamType.FORM),
			@ApiImplicitParam(name = "wardrobe", value = "衣橱大小", required = true, paramType = ParamType.FORM),
			@ApiImplicitParam(name = "storge", value = "收纳用品", required = true, paramType = ParamType.FORM),
			@ApiImplicitParam(name = "wardrobeImg", value = "衣橱图片多个,号分割", required = true, paramType = ParamType.FORM),
			@ApiImplicitParam(name = "remark", value = "需求说明", required = true, paramType = ParamType.FORM),
			@ApiImplicitParam(name = "doortime", value = "上门时间", required = true, paramType = ParamType.FORM),
			@ApiImplicitParam(name = "addressid", value = "地址id", required = true, paramType = ParamType.QUERY)
	})
	@ApiImplicitParam(name = "remark", value = "需求说明", required = true, paramType = ParamType.FORM)
	public void createSn() {
		if (null == getParaByJson("id")) {
			renderJson(Ret.fail(Constant.RETMSG, "项目id不能为空"));
			return;
		}
		Integer addressid = Integer.parseInt(getParaByJson("addressid"));
		Integer itemId = Integer.parseInt(getParaByJson("id"));
		String userCount = getParaByJson("userCount");
		String wardrobe = getParaByJson("wardrobe");
		String storge = getParaByJson("storge");
		String wardrobeImg = getParaByJson("wardrobeImg");
		String remark = getParaByJson("remark");
		String doortime = getParaByJson("doortime"); 
		if( addressid==null) {
			renderJson(Ret.fail(Constant.RETMSG, "地址id"));
			return;
		}
		if(StringUtil.isNullOrEmpty(userCount)) {
			renderJson(Ret.fail(Constant.RETMSG, "使用人数为空"));
			return;
		}
		if(StringUtil.isNullOrEmpty(wardrobe)){
			renderJson(Ret.fail(Constant.RETMSG, "衣橱为空"));
			return;
		}
		if(StringUtil.isNullOrEmpty(storge)){
			renderJson(Ret.fail(Constant.RETMSG, "收纳用品为空"));
			return;
		}
		if(StringUtil.isNullOrEmpty(wardrobeImg)){
			renderJson(Ret.fail(Constant.RETMSG, "衣橱图片为空"));
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		CustomerInfo customer = getLoginCustomer();
//		CustomerLevel level = customerLevelService.findById(customer.getLevelId());
		ItemInfo item = itemInfoService.findById(itemId);
		if (null == item) {
			renderJson(Ret.fail(Constant.RETMSG, "参数错误"));
			return;
		}
		CustomerAddress address = customerAddressService.findById(addressid);
		if (null == address) {
			renderJson(Ret.fail(Constant.RETMSG, "参数错误"));
			return;
		}
		CustomerOrder order = new CustomerOrder();
		order.setItemId(item.getId());
		order.setCustomerId(customer.getId());
		order.setOrderNo(DateUtil.format(new Date(), "yyMMddHHmmssSSS") + RandomUtil.randomNumbers(6));
		order.setItemName(item.getName());
		order.setPrice(item.getSaleAmount());
		order.setStatus(Constant.UNDONE);
		order.setSettleStatus(Constant.UNDONE);
		order.setCommission(BigDecimal.ZERO);
		order.setReachStatus("0");
		
		
		Calendar calendar = Calendar.getInstance();//日历对象
		calendar.setTime(new Date());//设置当前日期
		calendar.add(Calendar.DAY_OF_MONTH, 1);//天数加一，为-1的话是天数减1 
		order.setCountdown(calendar.getTime());
//		order.setOrderRate(level.getOrderRate());
		order.setAddtime(new Date());  
		order.setTelphone(address.getTelphone());
		if(null != address){
		    String result = "";
            String pinCity = CommonUtil.getPinyin(address.getCity());
            if("zhangsha".equalsIgnoreCase(pinCity)){
                result = "Changsha";
            }else {
                result = pinCity;
            }
            order.setCity(result);
        }

		 SysCity sysCity =sysCityService.findFirstByColumns(Columns.create("name",address.getCity()));
		 BigDecimal pay_amt = sysCity.getPrice();
		 order.setDeposit(pay_amt);
		
		order.save();

		//订单补充信息
		CustomerOrderOther customerOrderOther = new CustomerOrderOther();
		customerOrderOther.setStorge(storge);
		customerOrderOther.setRemark(remark);
		customerOrderOther.setUserCount(Integer.parseInt(userCount));
		customerOrderOther.setWardrobe(wardrobe);
		customerOrderOther.setWardrobeImg(wardrobeImg);
		customerOrderOther.setOrderId(order.getId());
		customerOrderOther.setAddtime(new Date());
		customerOrderOther.setOrderNo(order.getOrderNo());
		if(StrUtil.isNotBlank(doortime)) {
			try {
				customerOrderOther.setDoortime(sdf.parse((doortime)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} 
		customerOrderOther.setAddress(address.getAddress());
		customerOrderOther.setRealname(address.getRealname()); 	 
		customerOrderOther.setTelphone(address.getTelphone()); 
		customerOrderOther.save();
		renderRet(order);

		//客服发送短信通知
		sendMessageToService(order);
	}

	/**
	 * 客服发送短信通知
	 * @param order
	 */
	private void sendMessageToService(CustomerOrder order) {
		List<CustomerInfo> list = customerInfoService.findListByColumns(Columns.create("is_service", "1").eq("status", "0").isNotNull("telphone").like("city", order.getCity()));
		if(CollectionUtils.isEmpty(list)){
			return;
		}
		StringBuilder sb = new StringBuilder();
		list.forEach(e -> {
			sb.append(",").append(e.getTelphone());
		});
		if(sb.length() > 0){
			sb.deleteCharAt(0);
		}
		MessageConfig config = JbootConfigManager.me().get(MessageConfig.class);
		SMSUtil.send(sb.toString(), config.getUrl(), config.getAccessKeyId(), config.getAccessKeySecret(), config.getTemplateCode());
	}

	/**
	 * 上传订单图片
	 */
	@ApiOperation(value = "上传衣橱图片", httpMethod = "POST")
	public void uploadServer() {
		List<UploadFile> file = getFiles();
		if (!file.isEmpty()) {
			try { 
				String uploadPath = UploadUtil.uploadFile(file.get(0));
				renderJson(Ret.ok("imgurl", uploadPath));
			} catch (Exception e) {
				renderJson(Ret.fail(Constant.RETMSG,"上传失败,请稍后再试"));
			}
		} else {
			renderJson(Ret.fail(Constant.RETMSG,"文件为空"));
		}
	}

	/**
	 * 上传图片
	 */
	@ApiOperation(value = "上传图片", httpMethod = "POST")
	public void uploadOSS() {
		List<UploadFile> file = getFiles();
		if (!file.isEmpty()) {
			try {
				//上传原始图片到阿里云
				String uploadPath = UploadUtil.uploadFile(file.get(0));
				renderJson(Ret.ok("imgurl", uploadPath));
			} catch (Exception e) {
				renderJson(Ret.fail(Constant.RETMSG,"上传失败,请稍后再试"));
			}
		} else {
			renderJson(Ret.fail(Constant.RETMSG,"文件为空"));
		}
	}

	private String getCity(String city, CustomerAddress address) {
		String result = "";
		if(StringUtils.isEmpty(city)){
			String pinCity = CommonUtil.getPinyin(address.getCity());
			if("zhangsha".equalsIgnoreCase(pinCity)){
				result = "Changsha";
			}else {
				result = pinCity;
			}
		}else {
            result = city;
        }
		return result;
	}

	  @ApiOperation(value = "创建商城订单", httpMethod = "POST")
	  @ApiImplicitParams({
			  @ApiImplicitParam(name = "itemList", value = "商品信息", required = true, paramType = ParamType.QUERY),
			  @ApiImplicitParam(name = "addressId", value = "地址id", required = true, paramType = ParamType.QUERY),
			  @ApiImplicitParam(name = "amount", value = "总价格", required = true, paramType = ParamType.QUERY),
			  @ApiImplicitParam(name = "type", value = "是否直接购买    0  直接购买    1  购物车购买", required = true, paramType = ParamType.QUERY)})

	  public void createShoppingOrder() {
		  if (StringUtils.isEmpty(getParaByJson("itemList"))) {
			  renderJson(Ret.fail(Constant.RETMSG, "商品信息不能为空"));
			  return;
		  }
		  if (StringUtils.isEmpty(getParaByJson("addressId"))) {
			  renderJson(Ret.fail(Constant.RETMSG, "收货地址不能为空"));
			  return;
		  }
		  JSONArray array = JSONArray.parseArray(getParaByJson("itemList"));
		  Integer addressId = Integer.valueOf(getParaByJson("addressId"));
		  String type = getParaByJson("type");
		  BigDecimal amount = new BigDecimal(getParaByJson("amount"));
		  CustomerInfo customer = customerInfoService.findById(getLoginCustomer().getId());

		  //父级订单入库
		  ShoppingOrder shoppingOrder = new ShoppingOrder();
		  shoppingOrder.setCustomerId(customer.getId());
		  shoppingOrder.setPrice(amount);
		  shoppingOrder.setOrderNo(DateUtil.format(new Date(), "yyMMddHHmmssSSS") + RandomUtil.randomNumbers(6));
		  shoppingOrder.save();
		  for (int i = 0; i < array.size(); i++) {
			  JSONObject jsonObject = (JSONObject) JSONObject.parse(array.get(i).toString());
			  Integer itemId = jsonObject.getInteger("itemId");
			  Integer number = jsonObject.getInteger("number");

			  ItemInfo item = itemInfoService.findById(itemId);
			  if (null == item) {
				  renderJson(Ret.fail(Constant.RETMSG, "参数错误"));
				  break;
			  }
			  //子订单
			  ShoppingOrderItem orderItem = new ShoppingOrderItem();
			  orderItem.setCustomerId(customer.getId());
			  orderItem.setItemId(itemId);
			  orderItem.setOrderNo(DateUtil.format(new Date(), "yyMMddHHmmssSSS") + RandomUtil.randomNumbers(6));
			  orderItem.setItemNumber(number);
			  orderItem.setAddtime(new Date());
			  orderItem.setOrderId(shoppingOrder.getId());
			  orderItem.setPrice(item.getSaleAmount().multiply(new BigDecimal(number)));
			  orderItem.setStatus(ShoppingOrderEnum.UN_PAY.getValue());
			  orderItem.setSettleStatus(ShoppingOrderEnum.UN_PAY.getValue());

			  CustomerAddress address = customerAddressService.findById(addressId);
			  orderItem.setTelphone(address.getTelphone());
              if(null != address){
                  String result = "";
                  String pinCity = CommonUtil.getPinyin(address.getCity());
                  if("zhangsha".equalsIgnoreCase(pinCity)){
                      result = "Changsha";
                  }else {
                      result = pinCity;
                  }
                  orderItem.setCity(result);
              }
			  orderItem.setAddressId(addressId);
			  Calendar calendar = Calendar.getInstance();//日历对象
			  calendar.setTime(new Date());//设置当前日期
			  calendar.add(Calendar.MINUTE, 15);
			  orderItem.setCountdown(calendar.getTime());
			  orderItem.setType(type);
			  orderItem.save();
		  }

		  renderRet(shoppingOrder.getOrderNo());
	  }


}
