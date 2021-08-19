package com.qcws.shouna.controller;

import javax.servlet.http.HttpServletResponse;

import com.jfinal.plugin.activerecord.Db;
import com.qcws.shouna.shiro.ShiroUtils;
import com.qcws.shouna.shiro.UserDetail;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

import java.math.BigDecimal;

@RequestMapping("/admin")
public class IndexController extends JbootController {

	private static final String CITY_PARTNER = "cityPartner";

	public void index() {
		Long customerCount = 0L;
		Long orderCount = 0L;
		Long shoppingOrderCount = 0L;
		BigDecimal amount = null;
		BigDecimal shoppingAmount = null;

		UserDetail userDetail = ShiroUtils.getUserDetail();
		if(userDetail.getRoles().get(0).equals(CITY_PARTNER)){
			customerCount = Db.queryLong("SELECT COUNT(*) FROM customer_info where city ='" + userDetail.getUsername()+ "'");
			orderCount = Db.queryLong("SELECT COUNT(*) FROM customer_order where city ='" + userDetail.getUsername() + "'");
			amount =  Db.queryBigDecimal("select sum(price) from customer_order where settle_status='finish' and city ='" + userDetail.getUsername() + "'");
			shoppingOrderCount = Db.queryLong("select count(*) from t_shop_order_item where status='3' and settle_status='1' and city ='" + userDetail.getUsername() + "'");
			shoppingAmount =  Db.queryBigDecimal("select sum(price) from t_shop_order_item where status='3' and settle_status='1' and city ='" + userDetail.getUsername() + "'");
		}else {
			customerCount = Db.queryLong("SELECT COUNT(*) FROM customer_info");
			orderCount =  Db.queryLong("SELECT COUNT(*) FROM customer_order");
			amount =  Db.queryBigDecimal("select sum(price) from customer_order ");
			shoppingOrderCount = Db.queryLong("select count(*) from t_shop_order_item where status='3' and settle_status='1'");
			shoppingAmount =  Db.queryBigDecimal("select sum(price) from t_shop_order_item where status='3' and settle_status='1'");
		}

		int itemCount = Db.queryInt("SELECT COUNT(*) AS cname FROM item_info i LEFT JOIN item_category c ON i.category_id = c.id");



//		int joininfoCount = Db.queryInt("SELECT COUNT(*) FROM customer_joininfo");


		set("itemCounts",itemCount);

		set("customerCouts",customerCount);

//		set("joininfoCounts",joininfoCount);

		set("orderCounts",orderCount + shoppingOrderCount);


		if(null == amount){
			set("amount", new BigDecimal(0));
		}else {
			set("amount",amount.add(shoppingAmount == null ? new BigDecimal(0) : shoppingAmount));
		}

	}
	
	public void unauthorized() {
		HttpServletResponse response = getResponse();
		response.setStatus(403);
	}
	
}
