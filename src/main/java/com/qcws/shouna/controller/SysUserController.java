package com.qcws.shouna.controller;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import com.qcws.shouna.dto.MessageBox;
import com.qcws.shouna.dto.PageSearch;
import com.qcws.shouna.dto.SimpleDatagrid;
import com.qcws.shouna.model.SysUser;
import com.qcws.shouna.service.SysRoleService;
import com.qcws.shouna.service.SysUserService;
import com.qcws.shouna.utils.UploadUtil;

import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.io.IOException;
import java.util.Date;

@RequiresPermissions("sys_user")
@RequestMapping("/admin/sys/user")
public class SysUserController extends JbootController {

    @Inject private SysUserService sysUserService;
    @Inject private SysRoleService sysRoleService;

    public void index(){

    }

    public void grid(){
        PageSearch pageSearch = PageSearch.instance(this);
        Page<SysUser> page = sysUserService.paginateByColumns(
                pageSearch.getPageNumber(),
                pageSearch.getPageSize(),
                pageSearch.getColumns("username","realname","roles","status"),
                pageSearch.getOrderBy()
        );
        renderJson(new SimpleDatagrid(page));
    }

    public void add(){
        set("rs",sysRoleService.findAll());
    }

    public void edit(){
        set("rs",sysRoleService.findAll());
        int id = getParaToInt();
        set("s",sysUserService.findById(id));
    }

    public void save() throws IOException {
        UploadFile file = getFile("headimg");
        SysUser sysUser = getModel(SysUser.class,"s");
        if(file != null && file.getFile() != null){
            String uploadPath = UploadUtil.uploadFile(file);
            sysUser.setHeadimg(uploadPath);
        }
        sysUser.setAddtime(new Date());
        boolean bool = sysUser.saveOrUpdate();
        renderHtml(new MessageBox(bool).toString());
    }

    public void delete(){
        int id = getParaToInt();
        Boolean bool = sysUserService.deleteById(id);
        renderJson(bool ? Ret.ok() : Ret.fail());
    }

}
