package com.qcws.shouna.controller.api;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qcws.shouna.model.ItemCategory;
import com.qcws.shouna.model.ItemInfo;
import com.qcws.shouna.model.SysArticle;
import com.qcws.shouna.model.SysConfig;
import com.qcws.shouna.service.ItemCategoryService;
import com.qcws.shouna.service.ItemInfoService;
import com.qcws.shouna.service.SysArticleService;
import com.qcws.shouna.service.SysConfigService;

import io.jboot.db.model.Columns;
import io.jboot.web.controller.annotation.RequestMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RequestMapping("/api/website")
@Api(tags = "网站配置管理")
public class WebsiteController extends ApiController {
	
	@Inject private SysArticleService sysArticleService;
	@Inject private SysConfigService sysConfigService;
	@Inject private ItemInfoService itemInfoService;
	@Inject private ItemCategoryService itemCategoryService;
	
    @ApiOperation(value = "首页初始化数据", httpMethod = "GET")
	public void init() {
		SysConfig config = sysConfigService.findFirstByColumns(Columns.create("code", "HOME_INIT"));
		renderRet(JSON.parseObject(config.getContent()));
	}
    
    @ApiOperation(value = "模块列表（文章）", httpMethod = "GET")
	public void items() {
		String groupCode = getPara("code");
		Integer count = getParaToInt("num", 1);
		List<Record> rs = Db.find("select id, title, type, img, url from sys_article where group_code = ? limit 0, ?", groupCode, count);
		renderRet(rs);
	}
	
    @ApiOperation(value = "模块详情", httpMethod = "GET")
	public void detail() {
		Integer id = getParaToInt("id");
		SysArticle sysArticle = sysArticleService.findById(id);
		renderRet(sysArticle);
	}

	@ApiOperation(value = "衣橱配置", httpMethod = "GET")
	public void wardrobe(){
    	String str = Db.queryStr("SELECT content FROM sys_config WHERE id = 3");
		renderRet(JSONObject.parseObject(str));
	}
	

	@ApiOperation(value = "省市配置", httpMethod = "GET")
	public void address(){
		String str = Db.queryStr("SELECT content FROM sys_config WHERE id = 4");
		renderRet(JSONObject.parseObject(str));
	}
	
	
	@ApiOperation(value = "首页项目列表", httpMethod = "GET")
	public void itemList() {
		List<ItemInfo> itemInfoList = itemInfoService.findListByColumns(Columns.create("is_recommend","Y").eq("type",0),"sort");
		itemInfoList.forEach(itemInfo -> {
			ItemCategory itemCategory = itemCategoryService.findById(itemInfo.getCategoryId());
			if(null!=itemCategory) {
				itemInfo.put("categoryName", itemCategory.getName());
			}
		});
		renderJson(Ret.ok("items", itemInfoList));
	}

	@ApiOperation(value = "首页课程列表", httpMethod = "GET")
	public void courseList() {
		List<ItemInfo> itemInfoList = itemInfoService.findListByColumns(Columns.create("is_recommend","Y").eq("type",1),"sort");
		renderJson(Ret.ok("items", itemInfoList));
	}
	
}
