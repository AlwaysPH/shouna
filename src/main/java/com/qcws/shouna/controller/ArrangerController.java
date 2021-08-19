package com.qcws.shouna.controller;

import java.util.Date;
import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import com.qcws.shouna.dto.MessageBox;
import com.qcws.shouna.dto.PageSearch;
import com.qcws.shouna.dto.SimpleDatagrid;
import com.qcws.shouna.model.CustomerInfo;
import com.qcws.shouna.model.CustomerLevel;
import com.qcws.shouna.service.CustomerInfoService;
import com.qcws.shouna.service.CustomerLevelService;
import com.qcws.shouna.service.SysCityService;
import com.qcws.shouna.utils.UploadUtil;

import io.jboot.db.model.Columns;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * 收纳师管理
 * 
 * @author 唐小恕
 */
@RequiresPermissions("arranger")
@RequestMapping("/admin/arranger")
public class ArrangerController extends JbootController {

    @Inject
    CustomerLevelService customerLevelService;

	@Inject CustomerInfoService customerInfoService;
	
	@Inject SysCityService cityService;
	
    public void index(){

    }

    public void grid(){
        PageSearch pageSearch = PageSearch.instance(this);
        
        Page<CustomerInfo> page = customerInfoService.paginateByColumns(
                pageSearch.getPageNumber(),
                pageSearch.getPageSize(),
                pageSearch.getColumns("item_id","username","nickname","type","headimg","telphone","level_id","city","status").eq("type", 1),
                pageSearch.getOrderBy()
        );
        renderJson(new SimpleDatagrid(page));
    }

    public void add(){
    	
    }

    public void edit(){
    	set("cslist",cityService.findAll());
    	List<CustomerLevel> levelList = customerLevelService.findListByColumns(Columns.create("status", 0));
        set("levelList", levelList);
    	int id = getParaToInt();
        set("c",customerInfoService.findById(id));
    }

    public void save(){
		UploadFile file = getFile("headimg");
        CustomerInfo customerInfo = getModel(CustomerInfo.class,"c");
		if (file != null && file.getFile() != null) {
			String fileName = UploadUtil.uploadFile(file);
			customerInfo.setHeadimg(fileName);
		}
		customerInfo.setRegtime(new Date());
		customerInfo.setType(1);
        boolean bool = customerInfo.saveOrUpdate();
        renderHtml(new MessageBox(bool).toString());
    }

    public void relieve(){
        int id = getParaToInt();
        Boolean bool = Db.update("update customer_info set type = 0 where id = ?",id) > 0;
        renderJson(bool ? Ret.ok() : Ret.fail());
    }

}
