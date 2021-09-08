package com.qcws.shouna.timer;

import com.jfinal.aop.Inject;
import com.qcws.shouna.model.ShoppingOrderEnum;
import com.qcws.shouna.model.ShoppingOrderItem;
import com.qcws.shouna.service.ShoppingOrderItemService;
import io.jboot.components.schedule.annotation.Cron;
import io.jboot.db.model.Columns;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.Date;
import java.util.List;

@Slf4j
@Cron("*/2 * * * *")
public class UpdateShoppingOrderTimer implements Runnable {

    @Inject
    private ShoppingOrderItemService orderItemService;

    @Override
    public void run(){
        updateShoppingOrder();
    }

    /**
     * 遍历未付款、待收货、退货订单
     */
    public void updateShoppingOrder(){
        List<ShoppingOrderItem> unPayList = orderItemService.findListByColumns(Columns.create("status", ShoppingOrderEnum.UN_PAY.getValue()));
        List<ShoppingOrderItem> awaitReceiptList = orderItemService.findListByColumns(Columns.create("status", ShoppingOrderEnum.AWAIT_RECEIPT.getValue()));
        if(CollectionUtils.isNotEmpty(unPayList)){
            //遍历未付款列表
            for(ShoppingOrderItem order : unPayList){
                Date countdown = order.getCountdown();
                Date newDate = new Date();
                //付款时间超过15分钟，订单取消
                if(newDate.getTime() - countdown.getTime() > 15 * 60 * 1000){
                    log.info("订单号为{}的订单超过15分钟未付款，自动取消订单！", order.getOrderNo());
                    order.setStatus(ShoppingOrderEnum.CANCEL.getValue());
                    order.saveOrUpdate();
                }
            }
        }
        if(CollectionUtils.isNotEmpty(awaitReceiptList)){
            //遍历待收货列表
            for(ShoppingOrderItem order : awaitReceiptList){
                Date countdown = order.getCountdown();
                Date newDate = new Date();
                //发货后超过7天，自动收货
                if(newDate.getTime() - countdown.getTime() > 7 * 24 * 60 * 60 * 1000){
                    log.info("订单号为{}的订单超过7天未收货，自动收货！", order.getOrderNo());
                    order.setStatus(ShoppingOrderEnum.FINISH.getValue());
                    order.saveOrUpdate();
                }
            }
        }
    }

}
