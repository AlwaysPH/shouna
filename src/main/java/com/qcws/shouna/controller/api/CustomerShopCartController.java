package com.qcws.shouna.controller.api;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qcws.shouna.model.ShoppingCart;
import com.qcws.shouna.service.ShoppingCartService;
import io.jboot.db.model.Columns;
import io.jboot.support.swagger.ParamType;
import io.jboot.web.controller.annotation.RequestMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.Date;
import java.util.List;

/**
 * 购物车
 */
@RequestMapping("/api/shoppingCart")
@Api(tags = "购物车")
public class CustomerShopCartController extends ApiController{

    @Inject
    ShoppingCartService shoppingCartService;

    @ApiOperation(value = "购物车列表数据",httpMethod = "GET")
    public void getShoppingCartData(){
        Integer id = getLoginCustomer().getId();
        List<Record> records = Db.find("SELECT t.number, i.id itemId, i.`name`, i.imgurl, i.amount, i.sale_amount , s.content color, sc.content size FROM t_shopping_cart t LEFT JOIN item_info i ON t.item_id = i.id LEFT JOIN sys_config s ON i.color_id = s.id LEFT JOIN sys_config sc ON i.size_id = sc.id WHERE t.customer_id = " + id);
        renderJson(Ret.ok("data",records));
    }

    @ApiOperation(value = "添加购物车",httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "itemId",value = "商品id",paramType = ParamType.QUERY,required = true),
            @ApiImplicitParam(name = "number",value = "商品数量",paramType = ParamType.QUERY,required = true)
    })
    public void addShoppingCart(){
        Integer id = getLoginCustomer().getId();
        Integer itemId = Integer.valueOf(getParaByJson("itemId"));
        Integer number = Integer.valueOf(getParaByJson("number"));
        ShoppingCart cart = shoppingCartService.findFirstByColumns(Columns.create("item_id", itemId).eq("customer_id", getLoginCustomer().getId()));
        if(null != cart){
            Integer oldNum = cart.getNumber();
            cart.setNumber(oldNum + number);
            cart.saveOrUpdate();
        }else {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setCustomerId(id);
            shoppingCart.setItemId(itemId);
            shoppingCart.setNumber(number);
            shoppingCart.setAddtime(new Date());
            shoppingCartService.save(shoppingCart);
        }
        renderJson(Ret.ok());
    }

    @ApiOperation(value = "删除购物车",httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "itemId",value = "商品id",paramType = ParamType.QUERY,required = true),
            @ApiImplicitParam(name = "type",value = "0 删除商品    1 删除该商品的数量",paramType = ParamType.QUERY,required = true)
    })
    public void deleteShoppingCart(){
        Integer itemId = Integer.valueOf(getParaByJson("itemId"));
        String type = getParaByJson("type");
        ShoppingCart cart = shoppingCartService.findFirstByColumns(Columns.create("item_id", itemId).eq("customer_id", getLoginCustomer().getId()));
        if(null != cart){
            if("1".equalsIgnoreCase(type)){
                Integer oldNum = cart.getNumber();
                cart.setNumber(oldNum - 1);
                cart.saveOrUpdate();
            }else if("0".equalsIgnoreCase(type)){
                shoppingCartService.delete(cart);
            }
        }
        renderJson(Ret.ok());
    }
}
