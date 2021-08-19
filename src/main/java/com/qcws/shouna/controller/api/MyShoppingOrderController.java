package com.qcws.shouna.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qcws.shouna.model.*;
import com.qcws.shouna.service.*;
import com.qcws.shouna.utils.Constant;
import com.qcws.shouna.utils.DateUtil;
import io.jboot.db.model.Columns;
import io.jboot.support.swagger.ParamType;
import io.jboot.web.controller.annotation.RequestMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@RequestMapping("/api/myShoppingOrder")
@Api(tags = "我的商城订单")
public class MyShoppingOrderController extends ApiController {

    @Inject
    private ItemCommentService commentService;

    @Inject
    private ShoppingOrderService orderService;

    @Inject
    private ItemInfoService itemInfoService;

    @Inject
    private ShoppingReviewService reviewService;

    @Inject
    private ShoppingOrderItemService orderItemService;

    @Inject
    private SysConfigService configService;

    @ApiOperation(value = "我的商城订单",httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type",value = "订单状态 0 待付款  2 待收货  3 已完成  4 退款和售后  5 全部",paramType = ParamType.QUERY,required = true)
    })
    public void getShoppingOrder(){
        String type = getPara("type");
        if(StringUtils.isEmpty(type)){
            renderJson(Ret.fail());
            return;
        }
        Integer id = getLoginCustomer().getId();
        StringBuilder sb = new StringBuilder("SELECT ti.item_id itemId, ti.item_number itemNumber, ti.order_no orderNo, ti.price, ti.addtime, ti.status from t_shop_order_item ti WHERE ti.customer_id =");
        sb.append(id);
        if(type.equalsIgnoreCase(ShoppingOrderEnum.UN_PAY.getValue())){
            //待付款
            sb.append(" and ").append("ti.status=").append(ShoppingOrderEnum.UN_PAY.getValue());
        }else if(type.equalsIgnoreCase(ShoppingOrderEnum.AWAIT_RECEIPT.getValue())){
            //待收货
            sb.append(" and ").append("(ti.status=").append(ShoppingOrderEnum.AWAIT_SHIP.getValue())
                    .append(" or ti.status=").append(ShoppingOrderEnum.AWAIT_RECEIPT.getValue()).append(")")
                    .append(" and ti.settle_status = ").append(ShoppingOrderEnum.PAID.getValue());
        }else if(type.equalsIgnoreCase(ShoppingOrderEnum.FINISH.getValue())){
            //已完成
            sb.append(" and ").append("ti.status=").append(ShoppingOrderEnum.FINISH.getValue())
                    .append(" and ti.settle_status = ").append(ShoppingOrderEnum.PAID.getValue());
        }else if(type.equalsIgnoreCase(ShoppingOrderEnum.REFUND_SALE.getValue())){
            //退款和售后
            sb.append(" and ").append("(ti.status=").append(ShoppingOrderEnum.REFUND_ORDER.getValue())
                    .append(" or ti.status= ").append(ShoppingOrderEnum.PENDING_REFUND.getValue())
                    .append(" or ti.status= ").append(ShoppingOrderEnum.REFUND_FAILED.getValue()).append(")")
                    .append(" and ti.settle_status = ").append(ShoppingOrderEnum.PAID.getValue());
        }else {

        }
        List<Record> result = Db.find(sb.toString());
        if(CollectionUtils.isNotEmpty(result)){
            for(Record record : result){
                ItemInfo itemInfo = itemInfoService.findById(record.getInt("itemId"));
                record.set("itemName", itemInfo.getName());
                record.set("imgurl", itemInfo.getImgurl());
                record.set("color", configService.findById(itemInfo.getColorId()).getContent());
                record.set("size", configService.findById(itemInfo.getSizeId()).getContent());
                if(type.equalsIgnoreCase(ShoppingOrderEnum.REFUND_SALE.getValue())){
                    ShoppingReview review = reviewService.findFirstByColumns(Columns.create("order_no", record.get("orderNo")));
                    record.set("result", null == review ? null : review.getResult());
                    record.set("exNo", null == review ? null : review.getExNo());
                }
            }
        }
        renderJson(Ret.ok("data",result));
    }

    @ApiOperation(value = "订单退款操作",httpMethod = "post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo",value = "订单号",paramType = ParamType.QUERY,required = true),
            @ApiImplicitParam(name = "reason",value = "退款原因",paramType = ParamType.QUERY,required = true)
    })
    public void refund(){
        String orderNo = getParaByJson("orderNo");
        String reason = getParaByJson("reason");
        ShoppingOrderItem orderItem = orderItemService.findFirstByColumns(Columns.create("order_no", orderNo));
        //从订单生成7天之内允许退款
        if(!DateUtil.compareDate(orderItem.getAddtime(), new Date())){
            renderJson(Ret.fail());
            return;
        }
        orderItem.setStatus(ShoppingOrderEnum.REFUND_ORDER.getValue());
        orderItem.saveOrUpdate();

        ShoppingReview review = new ShoppingReview();
        review.setCustomerId(getLoginCustomer().getId());
        review.setOrderNo(orderNo);
        review.setReason(reason);
        review.setAddtime(new Date());
        review.saveOrUpdate();
        renderJson(Ret.ok());
    }

    @ApiOperation(value = "填写快递单号",httpMethod = "post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo",value = "订单号",paramType = ParamType.QUERY,required = true),
            @ApiImplicitParam(name = "exNo",value = "快递单号",paramType = ParamType.QUERY,required = true)
    })
    public void saveExNo(){
        String orderNo = getParaByJson("orderNo");
        String exNo = getParaByJson("exNo");
        ShoppingReview review = reviewService.findFirstByColumns(Columns.create("order_no", orderNo).eq("customer_id", getLoginCustomer().getId()).eq("result", "1"));
        if(null == review){
            renderJson(Ret.fail(Constant.RETMSG, "订单未审核，请联系客服"));
            return;
        }
        review.setExNo(exNo);
        review.saveOrUpdate();
        renderJson(Ret.ok());
    }

    @ApiOperation(value = "获取退款订单审核信息",httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo",value = "订单号",paramType = ParamType.QUERY,required = true)
    })
    public void getRefundOrderInfo(){
        String orderNo = getPara("orderNo");
        ShoppingReview review = reviewService.findFirstByColumns(Columns.create("order_no", orderNo).eq("customer_id", getLoginCustomer().getId()));
        renderJson(Ret.ok("data", review));
    }

    @ApiOperation(value = "收货",httpMethod = "post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo",value = "订单号",paramType = ParamType.QUERY,required = true)
    })
    public void receipt(){
        String orderNo = getParaByJson("orderNo");
        ShoppingOrderItem orderItem = orderItemService.findFirstByColumns(Columns.create("order_no", orderNo).eq("customer_id", getLoginCustomer().getId()));
        orderItem.setStatus(ShoppingOrderEnum.FINISH.getValue());
        orderItem.setOvertime(new Date());
        orderItem.saveOrUpdate();
        renderJson(Ret.ok());
    }

    private String getIds(List<Integer> ids) {
        StringBuilder sb = new StringBuilder();
        for(Integer i : ids){
            sb.append(",").append("'").append("i").append("'");
        }
        if(sb.length() > 0){
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }
}
