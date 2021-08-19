package com.qcws.shouna.controller.api;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qcws.shouna.common.resp.ArrangerResp;
import com.qcws.shouna.common.resp.OrderInfoResp;
import com.qcws.shouna.common.resp.OrderResp;
import com.qcws.shouna.common.resp.OrderarrangerBeanResp;
import com.qcws.shouna.model.*;
import com.qcws.shouna.service.*;
import com.qcws.shouna.utils.Constant;

import cn.hutool.core.util.StrUtil;
import io.jboot.db.model.Columns;
import io.jboot.support.swagger.ParamType;
import io.jboot.web.controller.annotation.RequestMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/***
 * 我的订单
 */
@RequestMapping("/api/myorder")
@Api(tags = "我的订单")
public class MyOrderController extends ApiController{

	//线上课程
	private static final String TYPE = "0";

    @Inject private CustomerOrderService customerOrderService;
    @Inject private ItemArrangerService itemArrangerService;
    @Inject private ItemInfoService itemInfoService;
    @Inject private CustomerOrderEvaluateService orderEvaluateService;
    @Inject private CustomerInfoService customerInfoService;
    @Inject private CustomerOrderOtherService orderOtherService;
    @Inject private SysCityService sysCityService;
    @Inject private ItemCourseService itemCourseService;
 
    private static final ObjectMapper MAPPER = new ObjectMapper();

   
    @ApiOperation(value = "取消订单",httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo",value = "订单号",paramType = ParamType.QUERY,required = true)
    })
    public void cancelOrder(){ 
    	String orderNo = getPara("orderNo");
    	CustomerOrder  order = customerOrderService.findFirstByColumns(Columns.create("order_no",orderNo)); 
    	order.setStatus("cancel");
        renderRet(order.update() );
    } 
    
    @ApiOperation(value = "整理师接单",httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo",value = "订单号",paramType = ParamType.QUERY,required = true)
    })
    public void arrangerOrder(){
    	CustomerInfo user = getLoginCustomer();
    	String orderNo = getPara("orderNo");
    	CustomerOrder  order = customerOrderService.findFirstByColumns(Columns.create("order_no",orderNo)); 
    	SysCity sysCity =sysCityService.findFirstByColumns(Columns.create("name",order.getCity()));
    	ItemArranger itemArranger = new ItemArranger();
        itemArranger.setOrderNo(orderNo);
        itemArranger.setArrangerId(user.getId());
        itemArranger.setRealname(user.getNickname());
        itemArranger.setAmount(sysCity.getPrice());
        itemArranger.setImgurl(user.getHeadimg());
        renderRet(itemArranger.save());
    }

    @ApiOperation(value = "选择整理师",httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "arrangerId",value = "整理师id",paramType = ParamType.QUERY,required = true),
            @ApiImplicitParam(name = "orderNo",value = "订单号",paramType = ParamType.QUERY,required = true)
    })
    public void  choiceArranger(){
        Integer arrangerId = getParaToInt("arrangerId");
        String orderNo = getPara("orderNo");
        ItemArranger itemArranger = itemArrangerService.findFirstByColumns(Columns.create("arranger_id",arrangerId).eq("order_no",orderNo));
        CustomerOrderEvaluate evaluate =  new CustomerOrderEvaluate();
        evaluate.setOrderNo(orderNo);
        evaluate.setArrangerId(arrangerId);
        evaluate.setAddtime(new Date());
        evaluate.setRealname(itemArranger.getRealname());
        evaluate.setStatus("0");
      	Db.update("update item_arranger set status = '1' where order_no = ? and arranger_id =  ?",orderNo,arrangerId);
      	Db.update("update customer_order set status = 'waitdoor' where order_no = ?",orderNo);
      	Boolean bool = evaluate.save();
        if(bool == false){
            renderJson(Constant.RETMSG,"整理师接单失败！");
        }
        renderJson(Ret.ok());
    }

    @ApiOperation(value = "我的课程",httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "type",value = "标识是否是线下课程   0 否  1 是",paramType = ParamType.QUERY,required = true)
	})
    public void courseList(){
        String type = getPara("type");
        Integer id = getLoginCustomer().getId();
        List<CustomerOrder> customerOrders = customerOrderService.findListByColumns(Columns.create("status","finish").eq("customer_id",id));
        List<ItemInfo>  courseList = new ArrayList<ItemInfo>();
        customerOrders.forEach(item -> {
            ItemInfo itemInfo = itemInfoService.findFirstByColumns(Columns.create("id",item.getItemId()).eq("type",1).eq("is_under", type));
            if(!type.equals(TYPE)){
                if(itemInfo != null){
                    courseList.add(itemInfo);
                }
            }else {
                if(null != itemInfo){
                    List<ItemCourse> courses = itemCourseService.findListByColumns(Columns.create("item_id", itemInfo.getId()));
                    itemInfo.setItemCourses(courses);
                    courseList.add(itemInfo);
                }
            }
        });
        renderJson("courseList",courseList);
    }
    
    
    @ApiOperation(value = "我的订单",httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status",value = "订单状态",paramType = ParamType.QUERY,required = true)
    })
    public void orderList(){
    	String status = getPara("status");
        CustomerInfo user = getLoginCustomer();
    	List<Record> result = null;
		JSONObject jsonObject = new JSONObject();
    	if(user!=null) {
			Integer customerId = user.getId();
			if(user.getType() == 2){
				//若为客服
				result = Db.find("select c.id id, c.customer_id customerId, c.order_no orderNo, c.item_id itemId, c.item_name itemName, " +
						"c.price price, c.`status` status, c.settle_status settleStatus, c.commission commission, c.order_rate orderRate, " +
						"c.countdown countdown, c.addtime addtime, c.overtime overtime, c.refund_amount refundAmount, c.telphone telphone, c.province province, " +
						"c.city city, c.advance_amount advanceAmount, c.final_amount finalAmount, c.remark remark, c.reach_status reachStatus, c.deposit deposit " +
						"from customer_order c LEFT JOIN item_info i on c.item_id = i.id where c.`status` = '"+status+"' and c.city like '%"+user.getCity()+"%' order by addtime desc");
			}else {
				result = Db.find("select c.id id, c.customer_id customerId, c.order_no orderNo, c.item_id itemId, c.item_name itemName, " +
						"c.price price, c.`status` status, c.settle_status settleStatus, c.commission commission, c.order_rate orderRate, " +
						"c.countdown countdown, c.addtime addtime, c.overtime overtime, c.refund_amount refundAmount, c.telphone telphone, c.province province, " +
						"c.city city, c.advance_amount advanceAmount, c.final_amount finalAmount, c.remark remark, c.reach_status reachStatus, c.deposit deposit " +
						"from customer_order c LEFT JOIN item_info i on c.item_id = i.id where c.`status` = '"+status+"' and c.customer_id = "+customerId+ " order by addtime desc");
			}
			jsonObject.put("userType", user.getType());
			jsonObject.put("order", result);
			renderRet(jsonObject);
//    		if(user.getType()==0) {
//    			Integer customerId = user.getId();
//    		    list = customerOrderService.findListByColumns(Columns.create("customer_id", customerId).eq("status", status), "addtime desc");
//    			if(status.equals("wait")){
//            		renderRet(getOrderarrangerList(list));
//    			}else {
//    				renderRet(getOrderInfoRespList(list,0));
//    			}
//        	}else {
//        		if(status.equals("wait")){
//        			 list = customerOrderService.findListByColumns(Columns.create("status", "wait").eq("city",user.getCity()), "addtime desc");
//        		     renderRet( getArrangerOrderList( list,user.getId()));
//        		}else {
//        			 list = new ArrayList<CustomerOrder>();
//        			 List<CustomerOrderEvaluate>  evaluateList  = orderEvaluateService.findListByColumns(Columns.create("arranger_id",  user.getId()), "addtime desc");
//        			 evaluateList.forEach(evaluate -> {
//        				CustomerOrder order = customerOrderService.findFirstByColumns(Columns.create("status", status).eq("order_no",evaluate.getOrderNo()));
//        				if(order != null){
//        					list.add(order);
//        	            }
//        	         });
//        			 renderRet(getOrderInfoRespList(list,1));
//        		}
//        	}
    	} 
    }
    
    
    @ApiOperation(value = "接单整理师",httpMethod = "GET")
    @ApiImplicitParams({ 
            @ApiImplicitParam(name = "orderNo",value = "订单号",paramType = ParamType.QUERY,required = true)
    })
    public void  arrangerList(){ 
        String orderNo = getPara("orderNo");
        List<ItemArranger> itemArrangers = itemArrangerService.findListByColumns(Columns.create("order_no",orderNo));
        List<ArrangerResp> list = new ArrayList<ArrangerResp>(); 
        for(ItemArranger i:itemArrangers) {
        	ArrangerResp arrangerResp = getArrangerResp(i);
        	Integer frequency = Db.queryInt("select count(*) from customer_order_evaluate where arranger_id = ?",i.getArrangerId());
    		Integer sum = Db.queryInt("select sum(point) from customer_order_evaluate where arranger_id = ?",i.getArrangerId());
    		arrangerResp.setFrequency(frequency);
    		if(frequency!=null&&sum!=null) {
    			arrangerResp.setApplause(sum/frequency);
    		}else {
    			arrangerResp.setApplause(100);
    		}
        	list.add(arrangerResp);
        }
        CustomerOrder order = customerOrderService.findFirstByColumns(Columns.create("order_no",orderNo));
        OrderarrangerBeanResp resp= new OrderarrangerBeanResp();
        OrderResp orderResp = getOrderResp(order);
        resp.setOrderResp(orderResp);
   	    resp.setArrangerRespList(list); 
        renderRet(resp); 
    }
    
    
    @ApiOperation(value = "订单详情", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id",value = "订单id",paramType = ParamType.FORM, required = true)
	})
	public void detail() {
    	Integer orderId = getParaToInt("id"); 
    	if( orderId==null){
			renderJson(Ret.fail(Constant.RETMSG, "订单Id为空"));
			return;
		} 
    	CustomerOrderOther order = orderOtherService.findFirstByColumns(Columns.create("order_id",orderId)); 
    	renderRet(order);  
    }
    
    
    @ApiOperation(value = "已到达",httpMethod = "GET")
    @ApiImplicitParams({ 
            @ApiImplicitParam(name = "orderNo",value = "订单号",paramType = ParamType.QUERY,required = true)
    })
    public void  reach(){ 
        String orderNo = getPara("orderNo");
        if( StrUtil.isBlank(orderNo)){
			renderJson(Ret.fail(Constant.RETMSG, "订单号为空"));
			return;
		}  
        CustomerOrder order = customerOrderService.findFirstByColumns(Columns.create("order_no",orderNo));
        order.setReachStatus("1");
        order.setRemark("已上门");
        Boolean bool = order.update();
        if(bool == false){
            renderJson(Constant.RETMSG,"修改状态失败！");
        }
        renderJson(Ret.ok());
    }
 
    private String secToTime(Date countdown) {
    	Date currentTime = new Date();
    	if(countdown!=null) { 
    		 if(currentTime.compareTo(countdown)<0) {
    			 DecimalFormat df = new DecimalFormat("00");
    			 long date1Ms = countdown.getTime(); 
    		     long date2Ms = currentTime.getTime(); 
    		     long difference = date1Ms - date2Ms;
    		     int hoursDiff = (int) (difference/(60 * 60 * 1000)); 
    		     int hoursMs = hoursDiff * 60 * 60 * 1000; 
    		     int minsDiff = (int) ((difference - hoursMs)/(60 * 1000)); 
    		     int minsMs = hoursMs + minsDiff * 60 * 1000; 
    		     int secDiff = (int) ((difference - minsMs)/1000); 
    		     return df.format(hoursDiff)+":"+ df.format(minsDiff)+":"+df.format(secDiff);
    		}
    	}
		return "00:00:00";
    }
    
    private List<ArrangerResp> getArrangerRespList(List<ItemArranger> list){
    	List<ArrangerResp> arrangerRespList  = new ArrayList<ArrangerResp>();
    	try { 
	    	for(ItemArranger i :list) {
	    		String  jsonStr = MAPPER.writeValueAsString(i); 
	    		ArrangerResp arrangerResp = MAPPER.readValue(jsonStr , ArrangerResp.class);
	    		arrangerRespList.add(arrangerResp);
	    	}
    	} catch (JsonProcessingException e) { 
    		e.printStackTrace();
    	}
    	return arrangerRespList;
    }
    
    private ArrangerResp getArrangerResp(ItemArranger itemArranger) { 
    	ArrangerResp arrangerResp = null;
		try {
			String  jsonStr = MAPPER.writeValueAsString(itemArranger);
			arrangerResp = MAPPER.readValue(jsonStr , ArrangerResp.class); 
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return arrangerResp;
    }
    
    
    private OrderResp getOrderResp(CustomerOrder order) {
    	OrderResp orderResp = new OrderResp();
    	orderResp.setId(order.getId());
    	orderResp.setCustomerId(order.getCustomerId());
    	orderResp.setOrderNo(order.getOrderNo());
    	orderResp.setItemId(order.getItemId());
    	orderResp.setItemName(order.getItemName());
    	orderResp.setPrice(order.getPrice());
    	orderResp.setStatus(order.getStatus());
    	orderResp.setSettleStatus(order.getSettleStatus());
    	orderResp.setCommission(order.getCommission());
    	orderResp.setOrderRate(order.getOrderRate());
    	orderResp.setCountdown(order.getCountdown());
    	orderResp.setAddtime(order.getAddtime());
    	orderResp.setOvertime(order.getOvertime());
    	orderResp.setRefundAmount(order.getRefundAmount());
    	orderResp.setTelphone(order.getTelphone());
    	orderResp.setProvince(order.getProvince());
    	orderResp.setCity(order.getCity());
    	orderResp.setCountdowns(secToTime(order.getCountdown()));
    	orderResp.setReachStatus(order.getReachStatus());
    	orderResp.setDeposit(order.getDeposit());
		return orderResp;
    }
    
    
   private List<OrderInfoResp>  getOrderInfoRespList(List<CustomerOrder> list,Integer userType) {
	   List<OrderInfoResp>  orderInfoList = new LinkedList<OrderInfoResp>(); 		
	   for(CustomerOrder order:list) {    
     	CustomerOrderEvaluate orderEvaluate  = orderEvaluateService.findFirstByColumns(Columns.create("order_no",order.getOrderNo())); 
     	if(orderEvaluate!=null) {
     		CustomerInfo c = customerInfoService.findById(orderEvaluate.getArrangerId()); 
     		OrderInfoResp info = new OrderInfoResp();
     		info.setId(order.getId());
     		info.setCustomerId(order.getCustomerId());
     		info.setOrderNo(order.getOrderNo());
     		info.setItemId(order.getItemId());
     		info.setItemName(order.getItemName());
     		info.setPrice(order.getPrice());
     		info.setStatus(order.getStatus());
     		info.setSettleStatus(order.getSettleStatus());
     		info.setCommission(order.getCommission());
     		info.setOrderRate(order.getOrderRate());
     		info.setCountdown(order.getCountdown());
     		info.setAddtime(order.getAddtime());
     		info.setOvertime(order.getOvertime());
     		info.setRefundAmount(order.getRefundAmount());
     		info.setTelphone(order.getTelphone());
     		info.setProvince(order.getProvince());
     		info.setCity(order.getCity());
     		info.setFinalAmount(order.getFinalAmount());
     		info.setAdvanceAmount(order.getAdvanceAmount());
     		info.setRemark(order.getRemark()); 
     		info.setDeposit(order.getDeposit());
     		if(order.getStatus().equals("waitdoor")) {
     			CustomerOrderOther orderOther = orderOtherService.findFirstByColumns(Columns.create("order_id",order.getId())); 
     			info.setCountdowns(secToTime(orderOther.getDoortime()));
     		}else {
     			info.setCountdowns(secToTime(order.getCountdown()));
     		}
     		info.setReachStatus(order.getReachStatus());
     		info.setDoorAmount(order.getPrice());
     		info.setArrangerId(c.getId());
     		info.setArrangername(c.getNickname());
     		info.setArrangerimgurl(c.getHeadimg());
     		info.setArrangerphone(c.getTelphone()); 
     		Integer frequency = Db.queryInt("select count(1) from customer_order_evaluate where arranger_id = ?",c.getId());
	   		Integer sum = Db.queryInt("select sum(point) from customer_order_evaluate where arranger_id = ?",c.getId());
	   		info.setFrequency(frequency); 
	   		info.setUserType(userType);
	   		if(frequency!=null&&sum!=null) {
	   			info.setApplause(sum/frequency);
	   		}else {
	   			info.setApplause(100);
	   		} 
	   		if(order.getStatus().equals("finish")) {
	   			if(Db.queryInt("select count(1) from item_comment where order_id = ?",order.getId())<1) {
	   				info.setCommentStatus(0);
	   			}else {
	   				info.setCommentStatus(1);
	   			}
	   		}
	   		orderInfoList.add(info);
     	}
     		
		}
	   return orderInfoList;
   }

   private List<OrderarrangerBeanResp> getOrderarrangerList(List<CustomerOrder> list) {
	   List<OrderarrangerBeanResp>  orderarrangerList = new LinkedList<OrderarrangerBeanResp>(); 		
	   for(CustomerOrder order:list) {
           ItemInfo itemInfo = itemInfoService.findFirstByColumns(Columns.create("id",order.getItemId()).eq("type",0));
           if(itemInfo != null){
          	 	OrderarrangerBeanResp resp= new OrderarrangerBeanResp();
          	 	OrderResp orderResp = getOrderResp(order); 
          	 	orderResp.setUserType(0);
         		List<ItemArranger> itemArrangers = itemArrangerService.findListByColumns(Columns.create("order_no",order.getOrderNo()));
         		List<ArrangerResp> arrangerRespList =getArrangerRespList(itemArrangers);
         		resp.setOrderResp(orderResp);
            	resp.setArrangerRespList(arrangerRespList); 
            	orderarrangerList.add(resp);  
           }
		}
	   return orderarrangerList;
   }
   
   
   private List<OrderarrangerBeanResp> getArrangerOrderList(List<CustomerOrder> list,Integer userId) {
	   List<OrderarrangerBeanResp>  orderarrangerList = new LinkedList<OrderarrangerBeanResp>(); 		
	   for(CustomerOrder order:list) {
           ItemInfo itemInfo = itemInfoService.findFirstByColumns(Columns.create("id",order.getItemId()).eq("type",0));
           if(itemInfo != null){
        	    OrderarrangerBeanResp resp= new OrderarrangerBeanResp();
        	    Integer count = Db.queryInt("select count(1) from item_arranger where arranger_id = ? and order_no = ?",userId,order.getOrderNo());
		        OrderResp orderResp = getOrderResp(order);
    		    orderResp.setOfferStatus(count);
    		    orderResp.setUserType(1);
         		resp.setOrderResp(orderResp);
         		resp.setArrangerRespList(new ArrayList<ArrangerResp>());
            	orderarrangerList.add(resp);  
           }
		}
	   return orderarrangerList;
   }
}
