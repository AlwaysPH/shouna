package com.qcws.shouna.controller.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.hazelcast.util.StringUtil;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qcws.shouna.common.resp.ItemCommentBeanResp;
import com.qcws.shouna.common.resp.ItemCommentResp;
import com.qcws.shouna.common.resp.ItemCourseBeanResp;
import com.qcws.shouna.dto.api.ItemDTO;
import com.qcws.shouna.model.CustomerInfo;
import com.qcws.shouna.model.ItemCategory;
import com.qcws.shouna.model.ItemComment;
import com.qcws.shouna.model.ItemCourse;
import com.qcws.shouna.model.ItemInfo;
import com.qcws.shouna.service.CustomerInfoService;
import com.qcws.shouna.service.CustomerOrderService;
import com.qcws.shouna.service.ItemCategoryService;
import com.qcws.shouna.service.ItemCommentService;
import com.qcws.shouna.service.ItemCourseService;
import com.qcws.shouna.service.ItemInfoService;
import com.qcws.shouna.utils.Constant;

import cn.hutool.core.util.StrUtil;
import io.jboot.db.model.Columns;
import io.jboot.support.swagger.ParamType;
import io.jboot.web.controller.annotation.RequestMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 项目信息
 */
@RequestMapping("/api/item")
@Api(tags = "项目信息")
public class ItemInfoController extends ApiController {

	@Inject private ItemInfoService itemInfoService;
	@Inject private ItemCategoryService itemCategoryService;
	@Inject private ItemCommentService itemCommentService;
	@Inject private CustomerInfoService customerInfoService;
	@Inject private ItemCourseService itemCourseService; 
	@Inject private CustomerOrderService customerOrderService;

	
	@ApiOperation(value = "最新订单",httpMethod = "GET") 
	public void neworder(){  
		Record record = Db.findFirst("SELECT concat( o.city, '用户', REPLACE ( o.telphone, SUBSTR( o.telphone, 4, 4 ), '****'),'【',o.item_name ,'】',(case i.type when 0 then '服务' when 1 then '课程' end),'下单成功' ) as content FROM customer_order o INNER JOIN item_info i ON o.item_id = i.id  WHERE 1 = 1  ORDER BY o.addtime DESC  LIMIT 1"); 
		renderRet(record);  
	}
	
	@ApiOperation(value = "项目列表", httpMethod = "GET")
	public void load() {
		List<ItemInfo> items = itemInfoService.findAll();
		List<ItemCategory> categs = itemCategoryService.findAll();
		List<ItemDTO> datas = new ArrayList<ItemDTO>();
		ItemDTO recommendItem = new ItemDTO();
		recommendItem.setId(0);
		recommendItem.setTitle("为你推荐");
		List<ItemDTO> recommendItems = new ArrayList<ItemDTO>();
		for (ItemInfo item : items) {
			ItemDTO child = new ItemDTO();
			child.setId(item.getId());
			child.setTitle(item.getName());
			child.setImage(item.getImgurl());
			recommendItems.add(child);
		}
		recommendItem.setItems(recommendItems);
		datas.add(recommendItem);
		for (ItemCategory categ : categs) {
			ItemDTO data = new ItemDTO();
			data.setId(categ.getId());
			data.setTitle(categ.getName());
			data.setImage(categ.getImg());
			List<ItemDTO> children = new ArrayList<ItemDTO>();
			for (ItemInfo item : items) {
				if (categ.getId().equals(item.getCategoryId())) {
					ItemDTO child = new ItemDTO();
					child.setId(item.getId());
					child.setTitle(item.getName());
					child.setImage(item.getImgurl());
					children.add(child);
				}
			}
			data.setItems(children);
		}
		renderJson(Ret.ok("datas", datas));
	}
	
	@ApiOperation(value = "首页课程列表", httpMethod = "GET")
	public void courseIndexList() {	
		List<ItemInfo> items = itemInfoService.findListByColumns(Columns.create("is_recommend","Y").eq("type",1)," sort");
		renderJson(Ret.ok("datas", items));
	}
	
	@ApiOperation(value = "项目详情", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id",value = "项目id",paramType = ParamType.FORM, required = true)
	})
	public void detail() {
		Integer itemId = getParaToInt("id");
		ItemInfo itemInfo = itemInfoService.findById(itemId);
		Integer total = Db.queryInt("select count(1) from item_comment where item_id = ?",itemId);
		Integer participantnum = Db.queryInt("select count(1) from customer_order where item_id = ?",itemId);
        Double score = Db.queryDouble("select COALESCE(sum(score)/count(1),0) from item_comment where item_id = ?",itemId);
		
		
		if (null == itemInfo) {
			renderJson(Ret.fail(Constant.RETMSG, "未查询到项目信息"));
			return;
		}
		Map<String,Object> map = new HashMap<>();
		map.put("itemInfo",itemInfo); 
		
		List<ItemComment> itemCommentList = itemCommentService.findListByColumns(Columns.create("item_id",itemId)," addtime desc");
		List<ItemCommentResp>  itemCommentListResp = new ArrayList<ItemCommentResp>();
		if(null!=itemCommentList && !itemCommentList.isEmpty()){
			itemCommentList.forEach(itemComment -> {
				CustomerInfo customerInfo = customerInfoService.findById(itemComment.getCustomerId());
				ItemInfo itemInfo1 = itemInfoService.findById(itemId);
				if(null != customerInfo) {
					itemComment.put("username", customerInfo.getUsername());
					itemComment.put("nickname", customerInfo.getNickname());
					itemComment.put("telephone", customerInfo.getTelphone());
					itemComment.put("headimg", customerInfo.getHeadimg());
					itemComment.put("item_name",itemInfo1.getName());
				}
				ItemCommentResp resp= new ItemCommentResp();
				resp.setItemComment(itemComment);
				//图片分割 
				resp.setImgs(itemComment.getImg().split(","));
				itemCommentListResp.add(resp);
			});
			map.put("itemComment",itemCommentListResp);	
			
			 
			//标签分组
			Map<String, Long> lables =  
						itemCommentList.stream().filter(assetVul->StrUtil.isNotBlank(assetVul.getLable())).collect(
						Collectors.groupingBy(ItemComment::getLable, Collectors.counting()));
			map.put("lables",lables);
			map.put("total",total);
			map.put("score",score);
			map.put("participantnum", participantnum);
		}else {
			map.put("itemComment",new ArrayList<>());
			map.put("lables","");
			map.put("score",score);
			map.put("total",total);
			map.put("participantnum", participantnum);
		}
			renderJson(Ret.ok("item", map));
	}

	@ApiOperation(value = "课程项目详情", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id",value = "项目id",paramType = ParamType.FORM, required = true)
	})
	public void kcDetail() {
		Integer itemId = getParaToInt("id");
		ItemInfo itemInfo = itemInfoService.findById(itemId);
		Integer total = Db.queryInt("select count(*) from item_comment where item_id = ?",itemId);
		Integer participantnum = Db.queryInt("select count(*) from customer_order where item_id = ?",itemId);
		if (null == itemInfo) {
			renderJson(Ret.fail(Constant.RETMSG, "未查询到项目信息"));
			return;
		}

		Map<String,Object> map = new HashMap<>();
		map.put("itemInfo",itemInfo);
		map.put("participantnum", participantnum);

		List<ItemCourse> itemCourseList = itemCourseService.findListByColumns(Columns.create("item_id",itemId)," sort");
		if(null!= itemCourseList && !itemCourseList.isEmpty()){
			//标签分组
			Map<String, List<ItemCourse>> itemCourseMap = itemCourseList.stream().collect(Collectors.groupingBy(t -> t.getGroupName()));
			ArrayList<ItemCourseBeanResp> itemCourse_list = new ArrayList<ItemCourseBeanResp>();
			for(Map.Entry<String, List<ItemCourse>> entry : itemCourseMap.entrySet()){
			    String mapKey = entry.getKey();
			    ItemCourseBeanResp resp = new ItemCourseBeanResp();
			    resp.setTitle(mapKey);
			    resp.setItemCourseList( entry.getValue());
			    itemCourse_list.add(resp);
			}
			
			map.put("itemCourse",itemCourse_list);
		}else{
			map.put("itemCourse",new ArrayList<>());
		}

		List<ItemComment> itemCommentList = itemCommentService.findListByColumns(Columns.create("item_id",itemId)," addtime desc");
		List<ItemCommentResp>  itemCommentListResp = new ArrayList<ItemCommentResp>();
		if(null!=itemCommentList && !itemCommentList.isEmpty()){
			itemCommentList.forEach(itemComment -> {
				CustomerInfo customerInfo = customerInfoService.findById(itemComment.getCustomerId());
				ItemInfo itemInfo1 = itemInfoService.findById(itemId);
				if(null != customerInfo) {
					itemComment.put("username", customerInfo.getUsername());
					itemComment.put("nickname", customerInfo.getNickname());
					itemComment.put("telephone", customerInfo.getTelphone());
					itemComment.put("headimg", customerInfo.getHeadimg());
					itemComment.put("item_name",itemInfo1.getName());
				}
				ItemCommentResp resp= new ItemCommentResp();
				resp.setItemComment(itemComment);
				//图片分割 
				resp.setImgs(itemComment.getImg().split(";"));
				itemCommentListResp.add(resp);
			});
			map.put("itemComment",itemCommentListResp);
			//标签分组
			Map<String, Long> lables = itemCommentList.stream().collect(Collectors.groupingBy(ItemComment::getLable, Collectors.counting()));
			ArrayList<ItemCommentBeanResp> itemComment_list = (ArrayList<ItemCommentBeanResp>) lables.entrySet().stream().map(e -> new ItemCommentBeanResp(e.getKey(),e.getValue())).collect(Collectors.toList());
		 
			map.put("lables",itemComment_list);
			map.put("total",total);
		}else {
			map.put("itemComment",new ArrayList<>());
			map.put("lables","");
			map.put("total",total);
		}
		renderJson(Ret.ok("item", map));
	}


	@ApiOperation(value = "添加评论", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "itemId", value = "项目id", required = true, paramType = ParamType.FORM),
			@ApiImplicitParam(name = "orderId", value = "订单id", required = true, paramType = ParamType.FORM),
			@ApiImplicitParam(name = "comment", value = "评论内容", required = true, paramType = ParamType.FORM),
			@ApiImplicitParam(name = "img", value = "图片多个,号分割", required = true, paramType = ParamType.FORM),
			@ApiImplicitParam(name = "star", value = "评价几星", required = true, paramType = ParamType.FORM) 
	})
	public void addComment() {
		String orderId = getParaByJson("orderId");
		String itemId = getParaByJson("itemId");
		String comment = getParaByJson("comment");
		String img = getParaByJson("img");
		String star = getParaByJson("star");
		if(StringUtil.isNullOrEmpty(itemId)){
			renderJson(Ret.fail(Constant.RETMSG, "项目ID不能为空"));
			return;
		}
		if(StringUtil.isNullOrEmpty(star)){
			renderJson(Ret.fail(Constant.RETMSG, "评价1-5星不能为空"));
			return;
		}
		ItemComment itemComment = new ItemComment();
		itemComment.setItemId(Integer.parseInt(itemId));
		itemComment.setComment(comment);
		itemComment.setImg(img);
		itemComment.setStart(star);
		itemComment.setScore((Integer.parseInt(star)*20));
		itemComment.setAddtime(new Date());
		itemComment.setCustomerId(getLoginCustomer().getId());
		if(StrUtil.isNotBlank(orderId)) {
			Integer orderid = Integer.parseInt(orderId);
			itemComment.setOrderId(orderid);
			Integer arrangerId = Db.queryInt("SELECT arranger_id FROM customer_order_evaluate WHERE order_no = ?",customerOrderService.findById(orderid).getOrderNo()); 
			itemComment.setArrangerId(arrangerId);
		} 
		itemComment.save();
		renderJson(Ret.ok());
	}


}
