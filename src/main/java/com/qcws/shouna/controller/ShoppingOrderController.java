package com.qcws.shouna.controller;

import com.jfinal.aop.Inject;
import com.qcws.shouna.dto.DbPageSearch;
import com.qcws.shouna.model.CustomerAddress;
import com.qcws.shouna.model.ItemInfo;
import com.qcws.shouna.model.ShoppingOrderItem;
import com.qcws.shouna.model.ShoppingReview;
import com.qcws.shouna.service.CustomerAddressService;
import com.qcws.shouna.service.ItemInfoService;
import com.qcws.shouna.service.ShoppingOrderItemService;
import com.qcws.shouna.service.ShoppingReviewService;
import com.qcws.shouna.shiro.ShiroUtils;
import com.qcws.shouna.shiro.UserDetail;
import io.jboot.db.model.Columns;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * 商城订单
 */
@RequiresPermissions("shopping_order")
@RequestMapping("/admin/shopping/order")
public class ShoppingOrderController extends JbootController {

    private static final String CITY_PARTNER = "cityPartner";

    @Inject
    private ShoppingOrderItemService orderItemService;

    @Inject
    private CustomerAddressService addressService;

    @Inject
    private ItemInfoService itemInfoService;

    @Inject
    private ShoppingReviewService reviewService;

    public void index(){
        set("cd",orderItemService.findAll());
    }

    public void grid(){
        DbPageSearch dbPageSearch = DbPageSearch.instance(this);
        UserDetail userDetail = ShiroUtils.getUserDetail();
        if(userDetail.getRoles().get(0).equals(CITY_PARTNER)){
            dbPageSearch.select("select co.*, ci.nickname, s.content color, c.content size, i.name itemName");
            dbPageSearch.from("from t_shop_order_item co LEFT JOIN customer_info ci ON co.customer_id = ci.id LEFT JOIN item_info i on co.item_id = i.id" +
                    " LEFT JOIN sys_config s on i.color_id = s.id LEFT JOIN sys_config c on i.size_id = c.id");
            dbPageSearch.addExcept("co.city = ?", DbPageSearch.EXIST_VAL, userDetail.getUsername());
            dbPageSearch.addExcept(" and (co.telphone like ?", DbPageSearch.ALLLIKE);
            dbPageSearch.addExcept("or co.order_no = ?");
            dbPageSearch.addExcept("or i.name like ?", DbPageSearch.ALLLIKE);
            dbPageSearch.addExcept("or co.addtime like ?", DbPageSearch.ALLLIKE);
            dbPageSearch.addExcept("or ci.nickname like ?)", DbPageSearch.ALLLIKE);
            dbPageSearch.orderBy("co.addtime desc");
        }else {
            dbPageSearch.select("select co.*, ci.nickname, s.content color, c.content size, i.name itemName");
            dbPageSearch.from("from t_shop_order_item co LEFT JOIN customer_info ci ON co.customer_id = ci.id LEFT JOIN item_info i on co.item_id = i.id" +
                    " LEFT JOIN sys_config s on i.color_id = s.id LEFT JOIN sys_config c on i.size_id = c.id");
            dbPageSearch.addExcept(" co.telphone like ?", DbPageSearch.ALLLIKE);
            dbPageSearch.addExcept("or co.order_no = ?");
            dbPageSearch.addExcept("or i.name like ?", DbPageSearch.ALLLIKE);
            dbPageSearch.addExcept("or co.addtime like ?", DbPageSearch.ALLLIKE);
            dbPageSearch.addExcept("or ci.nickname like ?", DbPageSearch.ALLLIKE);
            dbPageSearch.orderBy("co.addtime desc");
        }
        renderJson(dbPageSearch.toDataGrid());
    }

    public void details(){
        int id = getParaToInt();
        ShoppingReview review = reviewService.findById(id);
        ShoppingOrderItem order = new ShoppingOrderItem();
        if(null == review){
            order = orderItemService.findById(id);
        }else {
            order = orderItemService.findFirstByColumns(Columns.create("order_no", review.getOrderNo()));
        }
        ItemInfo itemInfo = itemInfoService.findById(order.getItemId());
        CustomerAddress address = addressService.findById(order.getAddressId());
        set("a", address);
        set("c",order);
        set("d", itemInfo.getName());
    }
}
