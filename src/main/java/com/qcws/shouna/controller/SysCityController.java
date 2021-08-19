package com.qcws.shouna.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import com.qcws.shouna.dto.MessageBox;
import com.qcws.shouna.dto.PageSearch;
import com.qcws.shouna.dto.SimpleDatagrid;
import com.qcws.shouna.model.SysCity;
import com.qcws.shouna.service.SysCityService;

import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequiresPermissions("sys_city")
@RequestMapping("/admin/sys/city")
public class SysCityController extends JbootController {

	@Inject
    SysCityService sysCityService;

    public void index(){

    }

    public void grid(){
        PageSearch pageSearch = PageSearch.instance(this);
        Page<SysCity> page = sysCityService.paginateByColumns(
          pageSearch.getPageNumber(),
          pageSearch.getPageSize(),
          pageSearch.getColumns("id","name","price"),
          pageSearch.getOrderBy());
        renderJson(new SimpleDatagrid(page));
    }

    public void edit(){
        int id = getParaToInt();
        set("s",sysCityService.findById(id));
    }

    public void save(){
    	SysCity sysCityInfo = getModel(SysCity.class,"s");
        boolean bool = sysCityInfo.saveOrUpdate();
        renderHtml(new MessageBox(bool).toString());
    }
}
