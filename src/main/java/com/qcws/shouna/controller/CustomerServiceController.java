package com.qcws.shouna.controller;

import com.jfinal.aop.Inject;
import com.qcws.shouna.dto.DbPageSearch;
import com.qcws.shouna.dto.MessageBox;
import com.qcws.shouna.model.CustomerEnum;
import com.qcws.shouna.model.CustomerInfo;
import com.qcws.shouna.model.CustomerLevel;
import com.qcws.shouna.model.CustomerOrder;
import com.qcws.shouna.service.CustomerInfoService;
import com.qcws.shouna.service.CustomerLevelService;
import com.qcws.shouna.service.CustomerOrderService;
import io.jboot.db.model.Columns;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.List;

@RequiresPermissions("customer_service_list")
@RequestMapping("/admin/customer/service")
public class CustomerServiceController extends JbootController {

    @Inject
    CustomerInfoService customerInfoService;
    
    public void index(){

    }

    public void grid(){
        DbPageSearch dbPageSearch = DbPageSearch.instance(this);
        dbPageSearch.select("select * ");
        dbPageSearch.from("from customer_info ");
        dbPageSearch.addExcept(" type = ?", DbPageSearch.EXIST_VAL, "2");
        dbPageSearch.addExcept(" and is_service = ?", DbPageSearch.EXIST_VAL, "1");
        dbPageSearch.addExcept(" and (username like ?", DbPageSearch.ALLLIKE);
        dbPageSearch.addExcept(" or nickname like ?", DbPageSearch.ALLLIKE);
        dbPageSearch.addExcept(" or telphone like ?)", DbPageSearch.ALLLIKE);
        dbPageSearch.orderBy(" regtime desc");
        renderJson(dbPageSearch.toDataGrid());
    }

    public void edit(){
        int id = getParaToInt();
        set("c",customerInfoService.findById(id));
    }

    public void save(){
        CustomerInfo customerInfo = getModel(CustomerInfo.class,"c");
        customerInfo.setTelphone(customerInfo.getTelphone());
        boolean bool = customerInfo.saveOrUpdate();
        renderHtml(new MessageBox(bool).toString());
    }

    public void ban(){
        CustomerInfo customerInfo = new CustomerInfo();
        int id = getParaToInt();
        customerInfo.setId(id);
        customerInfo.setStatus(1);
        renderHtml(new MessageBox(customerInfo.update()).toString());
//        renderJson(customerInfo.update() ? Ret.ok() : Ret.fail());
    }

    public void noban() {
        CustomerInfo customerInfo = new CustomerInfo();
        int id = getParaToInt();
        customerInfo.setId(id);
        customerInfo.setStatus(0);
        renderHtml(new MessageBox(customerInfo.update()).toString());
    }
}
