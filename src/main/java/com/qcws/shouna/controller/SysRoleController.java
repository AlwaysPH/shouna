package com.qcws.shouna.controller;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.qcws.shouna.dto.MessageBox;
import com.qcws.shouna.dto.PageSearch;
import com.qcws.shouna.dto.SimpleDatagrid;
import com.qcws.shouna.model.SysRole;
import com.qcws.shouna.service.SysResourceGroupService;
import com.qcws.shouna.service.SysResourceService;
import com.qcws.shouna.service.SysRoleService;

import cn.hutool.core.util.ArrayUtil;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import org.apache.shiro.authz.annotation.RequiresPermissions;

@RequiresPermissions("sys_role")
@RequestMapping("/admin/sys/role")
public class SysRoleController extends JbootController {

    @Inject SysRoleService sysRoleService;
    @Inject SysResourceGroupService sysResourceGroupService;
    @Inject SysResourceService sysResourceService;

    public void index(){

    }

    public void grid(){
        PageSearch pageSearch = PageSearch.instance(this);
        Page<SysRole> page = sysRoleService.paginateByColumns(
          pageSearch.getPageNumber(),
          pageSearch.getPageSize(),
          pageSearch.getColumns("name","code"),
          pageSearch.getOrderBy()
        );
        renderJson(new SimpleDatagrid(page));
    }

    public void add(){

    }

    public void edit(){
        int id = getParaToInt();
        set("s",sysRoleService.findById(id));
    }

    public void save(){
        SysRole sysRole = getModel(SysRole.class,"s");
        boolean bool = sysRole.saveOrUpdate();
        renderHtml(new MessageBox(bool).toString());
    }

    public void delete(){
        int id = getParaToInt();
        Boolean bool = sysRoleService.deleteById(id);
        renderJson(bool ? Ret.ok() : Ret.fail());
    }
    
    public void resource() {
    	Integer id = getParaToInt();
    	SysRole role = sysRoleService.findById(id);
    	set("rid", id);
    	set("rs", role.getPerms().split(","));
    	set("gs", sysResourceGroupService.findAll());
    	set("rList", sysResourceService.findAll());
    }
    
    public void saveRes() {
    	Integer rid = getParaToInt("rid");
    	String[] rcode = getParaValues("rcode");
    	String perms = ArrayUtil.join(rcode, ",");
    	SysRole role = new SysRole();
    	role.setId(rid);
    	role.setPerms(perms);
    	boolean success = sysRoleService.update(role);
    	renderHtml(new MessageBox(success).toString());
    }

}
