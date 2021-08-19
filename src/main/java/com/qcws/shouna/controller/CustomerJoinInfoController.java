package com.qcws.shouna.controller;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.qcws.shouna.dto.MessageBox;
import com.qcws.shouna.dto.PageSearch;
import com.qcws.shouna.dto.SimpleDatagrid;
import com.qcws.shouna.model.CustomerJoininfo;
import com.qcws.shouna.service.CustomerJoininfoService;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.Date;

@RequiresPermissions("customer_joininfo")
@RequestMapping("/admin/customer/joininfo")
public class CustomerJoinInfoController extends JbootController {

    @Inject
    CustomerJoininfoService customerJoininfoService;

    public void index(){

    }

    public void grid(){
        PageSearch pageSearch = PageSearch.instance(this);
        Page<CustomerJoininfo> page = customerJoininfoService.paginateByColumns(
          pageSearch.getPageNumber(),
          pageSearch.getPageSize(),
          pageSearch.getColumns("realname","itemname","text","recommend","telephone"),
          pageSearch.getOrderBy()
        );
        renderJson(new SimpleDatagrid(page));
    }

    public void add(){

    }

    public void edit(){
        int id = getParaToInt();
        set("c",customerJoininfoService.findById(id));
    }

    public void save(){
        CustomerJoininfo customerJoininfo = getModel(CustomerJoininfo.class,"c");
        customerJoininfo.setAddtime(new Date());
        boolean bool = customerJoininfo.saveOrUpdate();
        renderHtml(new MessageBox(bool).toString());
    }

    public void delete(){
        int id = getParaToInt();
        Boolean bool = customerJoininfoService.deleteById(id);
        renderJson(bool ? Ret.ok() : Ret.fail());
    }

}
