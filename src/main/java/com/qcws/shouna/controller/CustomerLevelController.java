package com.qcws.shouna.controller;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.qcws.shouna.dto.MessageBox;
import com.qcws.shouna.dto.PageSearch;
import com.qcws.shouna.dto.SimpleDatagrid;
import com.qcws.shouna.model.CustomerLevel;
import com.qcws.shouna.service.CustomerLevelService;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import org.apache.shiro.authz.annotation.RequiresPermissions;

@RequiresPermissions("customer_level")
@RequestMapping("/admin/customer/level")
public class CustomerLevelController extends JbootController {

    @Inject
    CustomerLevelService   customerLevelService;

    public void index(){

    }

    public void grid(){
        PageSearch pageSearch = PageSearch.instance(this);
        Page<CustomerLevel> customerLevelPage = customerLevelService.paginateByColumns(
                pageSearch.getPageNumber(),
                pageSearch.getPageSize(),
                pageSearch.getColumns("level","title","title_en","status"),
                pageSearch.getOrderBy()
        );
        renderJson(new SimpleDatagrid(customerLevelPage));

    }

    public void add(){

    }

    public void edit(){
        int id = getParaToInt();
        set("c",customerLevelService.findById(id));

    }

    public void save(){
        CustomerLevel customerLevel = getModel(CustomerLevel.class,"c");
        customerLevel.setStatus("0");
        boolean bool = customerLevel.saveOrUpdate();
        renderHtml(new MessageBox(bool).toString());

    }

    public void delete(){
        int id = getParaToInt();
        Boolean bool = customerLevelService.deleteById(id);
        renderJson(bool ? Ret.ok() : Ret.fail());
    }
}
