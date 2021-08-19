package com.qcws.shouna.controller;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import com.qcws.shouna.dto.PageSearch;
import com.qcws.shouna.dto.SimpleDatagrid;
import com.qcws.shouna.model.CustomerAddress;
import com.qcws.shouna.service.CustomerAddressService;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/admin/customer/address")
public class CustommerAddressController extends JbootController {

    @Inject
    CustomerAddressService customerAddressService;

    public void index(){

    }

    public void grid(){
        PageSearch pageSearch = PageSearch.instance(this);
        Page<CustomerAddress> page = customerAddressService.paginateByColumns(
                pageSearch.getPageNumber(),
                pageSearch.getPageSize(),
                pageSearch.getColumns("realname","telphone","address","customer_id","city"),
                pageSearch.getOrderBy()
        );
        renderJson(new SimpleDatagrid(page));
    }

}
