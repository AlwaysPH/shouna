package com.qcws.shouna.controller;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Db;
import com.qcws.shouna.dto.DbPageSearch;
import com.qcws.shouna.dto.MessageBox;
import com.qcws.shouna.model.CustomerOrder;
import com.qcws.shouna.service.CustomerInfoService;
import com.qcws.shouna.service.CustomerLevelService;
import com.qcws.shouna.service.CustomerOrderService;
import com.qcws.shouna.service.ItemInfoService;
import com.qcws.shouna.shiro.ShiroUtils;
import com.qcws.shouna.shiro.UserDetail;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import org.apache.shiro.authz.annotation.RequiresPermissions;

@RequiresPermissions("customer_order")
@RequestMapping("/admin/customer/order")
public class CustomerOrderController extends JbootController {

    private static final String CITY_PARTNER = "cityPartner";

    @Inject
    CustomerOrderService customerOrderService;

    @Inject
    ItemInfoService itemInfoService;

    @Inject
    CustomerLevelService customerLevelService;

    @Inject
    CustomerInfoService customerInfoService;

    public void index(){
        set("cd",customerOrderService.findAll());
    }

    public void grid(){
        DbPageSearch dbPageSearch = DbPageSearch.instance(this);
        UserDetail userDetail = ShiroUtils.getUserDetail();
        if(userDetail.getRoles().get(0).equals(CITY_PARTNER)){
            dbPageSearch.select("select co.*,ci.telphone, ci.nickname");
            dbPageSearch.from("from customer_order co LEFT JOIN customer_info ci ON co.customer_id = ci.id ");
            dbPageSearch.addExcept("co.city = ?", DbPageSearch.EXIST_VAL, userDetail.getUsername());
            dbPageSearch.addExcept(" and (ci.telphone like ?", DbPageSearch.ALLLIKE);
            dbPageSearch.addExcept("or co.order_no = ?");
            dbPageSearch.addExcept("or co.item_name like ?", DbPageSearch.ALLLIKE);
            dbPageSearch.addExcept("or co.addtime like ?", DbPageSearch.ALLLIKE);
            dbPageSearch.addExcept("or ci.nickname like ?)", DbPageSearch.ALLLIKE);
            dbPageSearch.orderBy("co.addtime desc");
        }else {
            dbPageSearch.select("select co.*,ci.telphone, ci.nickname");
            dbPageSearch.from("from customer_order co LEFT JOIN customer_info ci ON co.customer_id = ci.id ");
            dbPageSearch.addExcept(" ci.telphone like ?", DbPageSearch.ALLLIKE);
            dbPageSearch.addExcept("or co.order_no = ?");
            dbPageSearch.addExcept("or co.item_name like ?", DbPageSearch.ALLLIKE);
            dbPageSearch.addExcept("or co.addtime like ?", DbPageSearch.ALLLIKE);
            dbPageSearch.addExcept("or ci.nickname like ?", DbPageSearch.ALLLIKE);
            dbPageSearch.orderBy("co.addtime desc");
        }
        renderJson(dbPageSearch.toDataGrid());
    }

    public void details(){
        int id = getParaToInt();
        System.out.println(id);
        CustomerOrder customerOrder = customerOrderService.findById(id);

        set("c",customerOrder);
    }

    public void agree(){
        int id = getParaToInt();
        Integer row = Db.update("UPDATE customer_order SET status = 'fefundsucess' WHERE id = ?",id);
        renderHtml(new MessageBox(row > 0).toString());
    }


}
