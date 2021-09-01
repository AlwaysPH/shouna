package com.qcws.shouna.controller;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import com.qcws.shouna.dto.DbPageSearch;
import com.qcws.shouna.dto.MessageBox;
import com.qcws.shouna.dto.PageSearch;
import com.qcws.shouna.dto.SimpleDatagrid;
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

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;

@RequiresPermissions("customer_list")
@RequestMapping("/admin/customer/list")
public class CustomerInfoController extends JbootController {

    @Inject
    CustomerInfoService customerInfoService;
    
    @Inject
    CustomerOrderService customerOrderService;

    @Inject
    CustomerLevelService customerLevelService;

    public void index(){

    }

    public void grid(){
        DbPageSearch dbPageSearch = DbPageSearch.instance(this);
        dbPageSearch.select("select t1.*, t2.title ");
        dbPageSearch.from("from customer_info t1 LEFT JOIN customer_level t2 on t1.level_id = t2.id ");
        dbPageSearch.addExcept("t1.type = ?", DbPageSearch.EXIST_VAL, "0");
        dbPageSearch.addExcept("and (t1.username like ?", DbPageSearch.ALLLIKE);
        dbPageSearch.addExcept("or t1.nickname like ?", DbPageSearch.ALLLIKE);
        dbPageSearch.addExcept("or t1.telphone like ?)", DbPageSearch.ALLLIKE);
        dbPageSearch.orderBy(" t1.regtime desc");
        renderJson(dbPageSearch.toDataGrid());
    }

    public void edit(){
        int id = getParaToInt();
        set("c",customerInfoService.findById(id));
    }

    public void save(){
        CustomerInfo customerInfo = getModel(CustomerInfo.class,"c");
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
    
    public void joinArranger() {
    	CustomerInfo customerInfo = new CustomerInfo();
        int id = getParaToInt();
        List<CustomerOrder> customerOrders 
        	= customerOrderService.findListByColumns(Columns.create("customer_id",id));
        if (customerOrders.size() == 0) {
        	customerInfo.setId(id);
            customerInfo.setType(1);
            renderHtml(new MessageBox(customerInfo.update()).toString());
        } else {
        	renderHtml(new MessageBox(false).toString());
        }
        
    }

    public void joinCityPartner() {
        CustomerInfo customerInfo = new CustomerInfo();
        int id = getParaToInt();
        customerInfo.setId(id);
        customerInfo.setLevelId(customerLevelService.findFirstByColumns(Columns.create("title", CustomerEnum.PARTNER.getValue())).getId());
        customerInfo.setIsCityPartner("1");
        renderHtml(new MessageBox(customerInfo.update()).toString());
    }
    
    /**
     * 获取可用上级
     */
    public void editPid(){
        int id = getParaToInt();
        set("c",customerInfoService.findById(id));
        //查询用户等级为2（加盟商）的用户
        List<CustomerInfo> cs = customerInfoService.findListByColumns(Columns.create("level_id",2));
        set("cs", cs);
    }
    
    /**
     * 设置我的上级
     */
    public void setPid() {
    	CustomerInfo customerInfo = getModel(CustomerInfo.class,"c");
    	customerInfo.setPid(customerInfo.getPid());
		boolean bool = customerInfo.saveOrUpdate();
		renderHtml(new MessageBox(bool).toString());
    }

    /**
     * 获取等级信息
     */
    public void editLevel(){
        int id = getParaToInt();
        set("c",customerInfoService.findById(id));
        //查询等级信息
        List<CustomerLevel> cs = customerLevelService.findListByColumns(Columns.create("status", 0));
        set("cs", cs);
    }

    /**
     * 设置等级
     */
    public void setLevel() {
        CustomerInfo customerInfo = getModel(CustomerInfo.class,"c");
        customerInfo.setLevelId(customerInfo.getLevelId());
        boolean bool = customerInfo.saveOrUpdate();
        renderHtml(new MessageBox(bool).toString());
    }

    /**
     * 转客服
     */
    public void changeService(){
        int id = getParaToInt();
        set("c",customerInfoService.findById(id));
    }

    /**
     * 设置客服手机号
     */
    public void setService() {
        CustomerInfo customerInfo = getModel(CustomerInfo.class,"c");
        customerInfo.setTelphone(customerInfo.getTelphone());
        customerInfo.setIsService("1");
        customerInfo.setType(1);
        boolean bool = customerInfo.saveOrUpdate();
        renderHtml(new MessageBox(bool).toString());
    }

}
