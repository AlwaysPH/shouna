package com.qcws.shouna.controller;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.qcws.shouna.dto.MessageBox;
import com.qcws.shouna.dto.PageSearch;
import com.qcws.shouna.dto.SimpleDatagrid;
import com.qcws.shouna.model.SysConfig;
import com.qcws.shouna.service.SysConfigService;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import org.apache.shiro.authz.annotation.RequiresPermissions;

@RequiresPermissions("sys_config")
@RequestMapping("/admin/sys/config")
public class SysConfigController extends JbootController {

    @Inject
    SysConfigService sysConfigService;

    public void index(){

    }

    public void grid(){
        PageSearch pageSearch = PageSearch.instance(this);
        Page<SysConfig> page = sysConfigService.paginateByColumns(
                pageSearch.getPageNumber(),
                pageSearch.getPageSize(),
                pageSearch.getColumns("name","code"),
                pageSearch.getOrderBy()
        );
        renderJson(new SimpleDatagrid(page));
    }

    public void content() {
        Integer id = getParaToInt();
        set("c", sysConfigService.findById(id));
    }

    public void saveContent() {
        Integer id = getParaToInt("id");
        String content = getPara("content");
        Integer row = Db.update("update sys_config set content = ? where id = ?", content, id);
        boolean bool = false;
        if(row > 0){
            bool = true;
        }
        renderHtml(new MessageBox(bool).toString());
    }

    public void add(){

    }

    public void edit(){
        int id = getParaToInt();
        set("s",sysConfigService.findById(id));
    }

    public void save(){
        SysConfig sysConfig = getModel(SysConfig.class,"s");
        boolean bool = sysConfig.saveOrUpdate();
        renderHtml(new MessageBox(bool).toString());
    }

    public void delete(){
        int id = getParaToInt();
        Boolean bool = sysConfigService.deleteById(id);
        renderJson(bool ? Ret.ok() : Ret.fail());
    }

}
