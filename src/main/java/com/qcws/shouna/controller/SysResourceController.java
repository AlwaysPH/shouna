package com.qcws.shouna.controller;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.qcws.shouna.dto.DbPageSearch;
import com.qcws.shouna.dto.MessageBox;
import com.qcws.shouna.model.SysResource;
import com.qcws.shouna.service.SysResourceGroupService;
import com.qcws.shouna.service.SysResourceService;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import org.apache.shiro.authz.annotation.RequiresPermissions;

@RequiresPermissions("sys_resource")
@RequestMapping("/admin/sys/resource")
public class SysResourceController extends JbootController {

    @Inject
    SysResourceService sysResourceService;

    @Inject
    SysResourceGroupService sysResourceGroupService;

    public void index(){

    }

    public void grid(){
//        PageSearch pageSearch = PageSearch.instance(this);
//        Page<SysResource> page = sysResourceService.paginateByColumns(
//                pageSearch.getPageNumber(),
//                pageSearch.getPageSize(),
//                pageSearch.getColumns("name","code"),
//                pageSearch.getOrderBy()
//        );
//        renderJson(new SimpleDatagrid(page));
        DbPageSearch ps = DbPageSearch.instance(this);
        ps.select("select sr.*,srg.title");
        ps.from("from sys_resource sr LEFT JOIN sys_resource_group srg ON sr.group_id = srg.id");
        ps.addExcept("sr.name like ?", DbPageSearch.ALLLIKE);
        ps.addExcept("or sr.code = ?");
        ps.addExcept("or sr.link_url like ?", DbPageSearch.ALLLIKE);
        ps.addExcept("or srg.title like ?",DbPageSearch.ALLLIKE);
        ps.orderBy("sr.id asc");
        renderJson(ps.toDataGrid());
    }

    public void add(){
        set("cs",sysResourceGroupService.findAll());
    }

    public void edit(){
        int id = getParaToInt();
        set("cs",sysResourceGroupService.findAll());
        set("s",sysResourceService.findById(id));
    }

    public void save(){
        SysResource sysResource = getModel(SysResource.class,"s");
        boolean bool = sysResource.saveOrUpdate();
        renderHtml(new MessageBox(bool).toString());
    }

    public void delete(){
        int id = getParaToInt();
        Boolean bool = sysResourceService.deleteById(id);
        renderJson(bool ? Ret.ok() : Ret.fail());
    }


}
