package com.qcws.shouna.controller;

import com.qcws.shouna.dto.DbPageSearch;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import com.qcws.shouna.dto.PageSearch;
import com.qcws.shouna.dto.SimpleDatagrid;
import com.qcws.shouna.model.CustomerBill;
 
import com.qcws.shouna.service.CustomerBillService;

import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequiresPermissions("customer_bill")
@RequestMapping("/admin/customer/bill")
public class CustomerBillController extends JbootController {
	@Inject
	CustomerBillService customerBillService;
	
    public void index(){
        set("cp",customerBillService.findAll());
    }

    public void grid(){
		DbPageSearch dbPageSearch = DbPageSearch.instance(this);
		dbPageSearch.select("select a.amount, a.city, a.nickname ");
		dbPageSearch.from(" from (SELECT SUM(t1.price) amount, t2.nickname, t2.city FROM customer_order t1 LEFT JOIN customer_info t2 ON t1.city = t2.city");
		dbPageSearch.addExcept(" t2.is_city_partner = ?", DbPageSearch.EXIST_VAL, "1");
		dbPageSearch.addExcept(" AND t1.settle_status = ?", DbPageSearch.EXIST_VAL, "finish");
		dbPageSearch.addExcept(" AND t1.status = ?", DbPageSearch.EXIST_VAL, "finish");
		dbPageSearch.addExcept(" and (t2.nickname like ?", DbPageSearch.ALLLIKE);
		dbPageSearch.addExcept(" or t2.city like ?) ", DbPageSearch.ALLLIKE);
		dbPageSearch.addCommon(" ) a ");
		dbPageSearch.groupBy(new StringBuilder("a.nickname").append(", a.city").toString());
		renderJson(dbPageSearch.toDataGrid());
    }

}
