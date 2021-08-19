package com.qcws.shouna.controller;

import com.jfinal.aop.Inject;
import com.qcws.shouna.dto.DbPageSearch;
import com.qcws.shouna.dto.MessageBox;
import com.qcws.shouna.model.ShoppingOrderEnum;
import com.qcws.shouna.model.ShoppingOrderItem;
import com.qcws.shouna.service.ShoppingOrderItemService;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.Calendar;
import java.util.Date;

/**
 * 配送管理
 */
@RequiresPermissions("shopping_delivery")
@RequestMapping("/admin/shopping/delivery")
public class ShoppingDeliveryController extends JbootController {

    @Inject
    private ShoppingOrderItemService orderItemService;

    public void index(){
        set("cd",orderItemService.findAll());
    }

    public void grid(){
        DbPageSearch dbPageSearch = DbPageSearch.instance(this);
        dbPageSearch.select("select co.*, ci.nickname, s.content color, c.content size, i.name itemName");
        dbPageSearch.from("from t_shop_order_item co LEFT JOIN customer_info ci ON co.customer_id = ci.id LEFT JOIN item_info i on co.item_id = i.id" +
                " LEFT JOIN sys_config s on i.color_id = s.id LEFT JOIN sys_config c on i.size_id = c.id");
        dbPageSearch.addExcept(" co.settle_status= ?", DbPageSearch.EXIST_VAL, ShoppingOrderEnum.PAID.getValue());
        dbPageSearch.addExcept(" and co.status= ?", DbPageSearch.EXIST_VAL, ShoppingOrderEnum.AWAIT_SHIP.getValue());
        dbPageSearch.addExcept(" and (co.telphone like ?", DbPageSearch.ALLLIKE);
        dbPageSearch.addExcept("or co.order_no = ?");
        dbPageSearch.addExcept("or i.name like ?", DbPageSearch.ALLLIKE);
        dbPageSearch.addExcept("or co.addtime like ?", DbPageSearch.ALLLIKE);
        dbPageSearch.addExcept("or ci.nickname like ?)", DbPageSearch.ALLLIKE);
        dbPageSearch.orderBy("co.addtime desc");
        renderJson(dbPageSearch.toDataGrid());
    }

    public void send(){
        Integer id = getParaToInt();
        ShoppingOrderItem order = orderItemService.findById(id);
        if(null == order){
            renderHtml(new MessageBox(false).toString());
            return;
        }
        if(!order.getStatus().equals(ShoppingOrderEnum.AWAIT_SHIP.getValue())
            || !order.getSettleStatus().equals(ShoppingOrderEnum.PAID.getValue())){
            renderHtml(new MessageBox(false).toString());
            return;
        }
        order.setStatus(ShoppingOrderEnum.AWAIT_RECEIPT.getValue());
        //收货最后时间为发货后7天
        Calendar calendar = Calendar.getInstance();//日历对象
        calendar.setTime(new Date());//设置当前日期
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        order.setCountdown(calendar.getTime());
        renderHtml(new MessageBox(order.saveOrUpdate()).toString());
    }
}
