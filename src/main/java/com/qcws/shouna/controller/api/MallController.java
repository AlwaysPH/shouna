package com.qcws.shouna.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.qcws.shouna.model.ItemComment;
import com.qcws.shouna.model.ItemInfo;
import com.qcws.shouna.service.CustomerOrderService;
import com.qcws.shouna.service.ItemCommentService;
import com.qcws.shouna.service.ItemInfoService;
import com.qcws.shouna.service.SysConfigService;
import io.jboot.db.model.Columns;
import io.jboot.support.swagger.ParamType;
import io.jboot.web.controller.annotation.RequestMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商城
 */
@RequestMapping("/api/mall")
@Api(tags = "商城")
public class MallController extends ApiController{

    @Inject
    ItemInfoService itemInfoService;

    @Inject
    CustomerOrderService customerOrderService;

    @Inject
    ItemCommentService itemCommentService;

    @Inject
    SysConfigService sysConfigService;

    @ApiOperation(value = "商品列表",httpMethod = "GET")
    public void getMallProduct(){
        List<ItemInfo> items = itemInfoService.findListByColumns(Columns.create("type", 2));
        for(ItemInfo itemInfo : items){
            Long number = Db.queryLong("select count(*) from customer_order where status='finish' and settle_status='finish' and item_id="+itemInfo.getId());
            itemInfo.setSellNumber(number);
        }
        renderJson(Ret.ok("data",items));
    }

    @ApiOperation(value = "商品详情",httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "商品id",paramType = ParamType.QUERY,required = true)
    })
    public void getProductDetail(){
        Integer id = getParaToInt("id");
        ItemInfo itemInfo = itemInfoService.findById(id);
        List<ItemComment> comments = itemCommentService.findListByColumns(Columns.create("item_id", itemInfo.getId()), "addtime desc");
        itemInfo.setComments(comments);
        //整合code一样的所有商品
        List<ItemInfo> list = itemInfoService.findListByColumns(Columns.create("code", itemInfo.getCode()).notIn("id", itemInfo.getId()));
        dealWithData(list, itemInfo);
        renderJson(Ret.ok("data",itemInfo));
    }

    private void dealWithData(List<ItemInfo> list, ItemInfo itemInfo) {
        JSONArray array = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("itemId", itemInfo.getId());
        jsonObject.put("amount", itemInfo.getAmount());
        jsonObject.put("saleAmount", itemInfo.getSaleAmount());
        jsonObject.put("sellNumber", Db.queryLong("select count(*) from customer_order where status='finish' and settle_status='finish' and item_id="+itemInfo.getId()));
        jsonObject.put("color", sysConfigService.findById(itemInfo.getColorId()).getContent());
        jsonObject.put("size", sysConfigService.findById(itemInfo.getSizeId()).getContent());
        array.add(jsonObject);

        if(CollectionUtils.isNotEmpty(list)){
            for(ItemInfo info : list){
                JSONObject json = new JSONObject();
                json.put("itemId", info.getId());
                json.put("amount", info.getAmount());
                json.put("saleAmount", info.getSaleAmount());
                json.put("sellNumber", Db.queryLong("select count(*) from customer_order where status='finish' and settle_status='finish' and item_id="+info.getId()));
                json.put("color", sysConfigService.findById(info.getColorId()).getContent());
                json.put("size", sysConfigService.findById(info.getSizeId()).getContent());
                array.add(json);
            }
        }
        itemInfo.setArray(array);
    }
}
