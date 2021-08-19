package com.qcws.shouna.controller;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import com.qcws.shouna.dto.PageSearch;
import com.qcws.shouna.dto.SimpleDatagrid;
import com.qcws.shouna.model.SysOptLog;
import com.qcws.shouna.service.SysOptLogService;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import org.apache.shiro.authz.annotation.RequiresPermissions;

@RequiresPermissions("sys_optlog")
@RequestMapping("/admin/sys/optlog")
public class SysOptLogController extends JbootController {

    @Inject
    SysOptLogService sysOptLogService;

    public void index(){

    }

    public void grid(){
        PageSearch pageSearch = PageSearch.instance(this);
        Page<SysOptLog> page = sysOptLogService.paginateByColumns(
                pageSearch.getPageNumber(),
                pageSearch.getPageSize(),
                pageSearch.getColumns("name","code"),
                pageSearch.getOrderBy()
        );
        renderJson(new SimpleDatagrid(page));
    }

}
