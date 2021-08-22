package com.qcws.shouna.controller.api;


import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.hazelcast.util.StringUtil;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qcws.shouna.config.WxConfig;
import com.qcws.shouna.dto.api.WxPayDto;
import com.qcws.shouna.dto.api.WxPayResDto;
import com.qcws.shouna.model.*;
import com.qcws.shouna.service.*;
import com.qcws.shouna.utils.Constant;
import com.qcws.shouna.utils.QRCodeUtils;
import com.qcws.shouna.utils.wx.MobileUtil;
import com.qcws.shouna.utils.wx.PayCommonUtil;

import io.jboot.app.config.JbootConfigManager;
import io.jboot.db.model.Columns;
import io.jboot.support.swagger.ParamType;
import io.jboot.web.controller.annotation.RequestMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

/**
 * 支付管理
 */
@RequestMapping("/api/payment")
@Api(tags = "支付管理")
@Slf4j
public class PaymentController extends ApiController {

    @Inject
    private CustomerOrderService customerOrderService;
    @Inject
    private ItemInfoService itemInfoService;
    @Inject
    private WxPayService wxPayService;
    @Inject
    private CustomerInfoService customerInfoService;
    @Inject
    private SysCityService sysCityService;
    @Inject
    private CustomerOrderEvaluateService orderEvaluateService;
    @Inject
    private CustomerLevelService customerLevelService;

    @Inject
    private ShoppingOrderService shoppingOrderService;

    @Inject
    private ShoppingOrderItemService orderItemService;

    @Inject
    private ShoppingCartService cartService;



    @ApiOperation(value = "微信发起支付", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "微信授权code", required = true, paramType = ParamType.FORM),
            @ApiImplicitParam(name = "orderNo", value = "订单编号", required = true, paramType = ParamType.FORM)
    })
    public void doPay() throws Exception{
        //获取code 这个在微信支付调用时会自动加上这个参数无须设置
        String code = getParaByJson("code");
        String orderNo = getParaByJson("orderNo");
        if (StringUtil.isNullOrEmpty(orderNo)) {
            renderJson(Ret.fail(Constant.RETMSG, "订单编号为空"));
            return;
        }

        WxConfig config = JbootConfigManager.me().get(WxConfig.class);

        WxPayDto wxPayDto = new WxPayDto();
        wxPayDto.setCode(code);
        wxPayDto.setOrderNo(orderNo);
        wxPayDto.setAppId(config.getAppId());
        wxPayDto.setApiKey(config.getApiKey());
        wxPayDto.setAppSecret(config.getSecret());
        wxPayDto.setMerId(config.getMerchantId());
        wxPayDto.setServerUrl(config.getServerUrl());
        WxPayResDto wxPayResDto = wxPayService.dopay(wxPayDto,getIPAddress());
        if(wxPayResDto.getCode().equals("SUCCESS")){
            renderJson(Ret.ok("items", wxPayResDto));
        }else{
            renderJson(Ret.fail(Constant.RETMSG, wxPayResDto.getMsg()));
        }
    }

    @ApiOperation(value = "商城订单发起微信支付", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "微信授权code", required = true, paramType = ParamType.FORM),
            @ApiImplicitParam(name = "orderNo", value = "订单编号", required = true, paramType = ParamType.FORM)
    })
    public void shoppingDoPay() throws Exception{
        //获取code 这个在微信支付调用时会自动加上这个参数无须设置
        String code = getParaByJson("code");
        String orderNo = getParaByJson("orderNo");
        if (StringUtil.isNullOrEmpty(orderNo)) {
            renderJson(Ret.fail(Constant.RETMSG, "订单编号为空"));
            return;
        }

        WxConfig config = JbootConfigManager.me().get(WxConfig.class);

        WxPayDto wxPayDto = new WxPayDto();
        wxPayDto.setCode(code);
        wxPayDto.setOrderNo(orderNo);
        wxPayDto.setAppId(config.getAppId());
        wxPayDto.setApiKey(config.getApiKey());
        wxPayDto.setAppSecret(config.getSecret());
        wxPayDto.setMerId(config.getMerchantId());
        wxPayDto.setServerUrl(config.getServerUrl());
        WxPayResDto wxPayResDto = wxPayService.doShoppingPay(wxPayDto,getIPAddress());
        if(wxPayResDto.getCode().equals("SUCCESS")){
            renderJson(Ret.ok("items", wxPayResDto));
        }else{
            renderJson(Ret.fail(Constant.RETMSG, wxPayResDto.getMsg()));
        }
    }


    @ApiOperation(value = "预付款", httpMethod = "POST")
    @ApiImplicitParams({

            @ApiImplicitParam(name = "orderNo", value = "订单编号", required = true, paramType = ParamType.FORM),
            @ApiImplicitParam(name = "advanceAmount", value = "预付款金额", required = true, paramType = ParamType.FORM)
    })
    public void advancePay() throws Exception{
        String orderNo = getParaByJson("orderNo");
        String advanceAmount = getParaByJson("advanceAmount");

        if (StringUtil.isNullOrEmpty(orderNo)) {
            renderJson(Ret.fail(Constant.RETMSG, "订单编号为空"));
            return;
        }
        if (StringUtil.isNullOrEmpty(advanceAmount)) {
            renderJson(Ret.fail(Constant.RETMSG, "预付款为空"));
            return;
        }
        CustomerOrder order = customerOrderService.findFirstByColumns(Columns.create("order_no",orderNo));
        order.setAdvanceAmount(new BigDecimal(advanceAmount));
        order.update();

        WxConfig config = JbootConfigManager.me().get(WxConfig.class);
        WxPayDto wxPayDto = new WxPayDto();
        wxPayDto.setOrderNo(orderNo);
        wxPayDto.setAppId(config.getAppId());
        wxPayDto.setApiKey(config.getApiKey());
        wxPayDto.setAppSecret(config.getSecret());
        wxPayDto.setMerId(config.getMerchantId());
        wxPayDto.setServerUrl(config.getServerUrl());
        String urlCode = wxPayService.doQrCode(wxPayDto,new BigDecimal(advanceAmount),"advance_","/api/payment/doAdvanceBack",getIPAddress());
        renderJson(Ret.ok("urlCode", QRCodeUtils.encode(urlCode)));
    }


    @ApiOperation(value = "尾款", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "订单编号", required = true, paramType = ParamType.FORM),
            @ApiImplicitParam(name = "finalAmount", value = "尾款金额", required = true, paramType = ParamType.FORM)
    })
    public void finalPay() throws Exception{
        String orderNo = getParaByJson("orderNo");
        String finalAmount = getParaByJson("finalAmount");
        if (StringUtil.isNullOrEmpty(orderNo)) {
            renderJson(Ret.fail(Constant.RETMSG, "订单编号为空"));
            return;
        }
        if (StringUtil.isNullOrEmpty(finalAmount)) {
            renderJson(Ret.fail(Constant.RETMSG, "尾款为空"));
            return;
        }
        CustomerOrder order = customerOrderService.findFirstByColumns(Columns.create("order_no",orderNo));
        order.setFinalAmount(new BigDecimal(finalAmount));
        order.update();


        WxConfig config = JbootConfigManager.me().get(WxConfig.class);

        WxPayDto wxPayDto = new WxPayDto();
        wxPayDto.setOrderNo(orderNo);
        wxPayDto.setAppId(config.getAppId());
        wxPayDto.setApiKey(config.getApiKey());
        wxPayDto.setAppSecret(config.getSecret());
        wxPayDto.setMerId(config.getMerchantId());
        wxPayDto.setServerUrl(config.getServerUrl());
        String urlCode = wxPayService.doQrCode(wxPayDto,new BigDecimal(finalAmount),"final_","/api/payment/doFinalBack",getIPAddress());
        renderJson(Ret.ok("urlCode", QRCodeUtils.encode(urlCode)));
    }

    /**
     * 微信支付回调
     */
    @ApiOperation(value = "微信支付回调", httpMethod = "POST")
    public void doBack() {
        WxConfig config = JbootConfigManager.me().get(WxConfig.class);
        String resXml = "";
        try {
            // 解析XML
            Map<String, String> map = MobileUtil.parseXml(this.getRequest());
            String return_code = map.get("return_code");// 状态
            String return_msg = map.get("return_msg");
            if (return_code.equals("SUCCESS")) {
                SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>(map);
                String sign = PayCommonUtil.createSign("UTF-8", packageParams,
                        config.getApiKey());
                String return_sign = map.get("sign");
                if(sign.equals(return_sign)) {
                    String result_code = map.get("result_code");
                    String err_code_des = map.get("err_code_des");
                    String out_trade_no = map.get("out_trade_no");// 订单号
                    //查询订单
                    CustomerOrder order = customerOrderService.findFirstByColumns(Columns.create("order_no",out_trade_no));
                    if(result_code.equals("SUCCESS")) {
                        BigDecimal total_fee = new BigDecimal(map.get("total_fee"));
                        if(null == order){
                            //商城订单
                            ShoppingOrder shoppingOrder = shoppingOrderService.findFirstByColumns(Columns.create("order_no",out_trade_no));
                            if(null != shoppingOrder){
                                //商城订单处理,父级订单付款
                                dealWithShoppingOrder(total_fee, out_trade_no, shoppingOrder.getCustomerId());
                            }else {
                                ShoppingOrderItem orderItem = orderItemService.findFirstByColumns(Columns.create("order_no", out_trade_no));
                                //商城订单处理，子级订单付款
                                dealWithShoppingOrder(total_fee, out_trade_no, orderItem.getCustomerId());
                            }
                        }else {
                            ItemInfo  info = itemInfoService.findById(order.getItemId());
                            BigDecimal order_amt;
                            if(info.getType()==0) {
                                //收纳订单
                                SysCity sysCity =sysCityService.findFirstByColumns(Columns.create("english_name",order.getCity()));
                                order_amt = sysCity.getPrice().multiply(new BigDecimal(100));
                                if (order_amt.compareTo(total_fee)==0) {
                                    finish(order,out_trade_no);
                                } else {
                                    cancel(order,total_fee,order_amt);
                                }
                                Db.update("update customer_order set status = 'wait' where order_no = ?",out_trade_no);
                            }else if(info.getType() == 1) {
                                //课程订单
                                order_amt = order.getPrice().multiply(new BigDecimal(100));
                                if (order_amt.compareTo(total_fee)==0) {
                                    finish(order,out_trade_no);
                                } else {
                                    cancel(order,total_fee,order_amt);
                                }
                                commission(order);
                            }
                        }
                    }else {
                        log.info("doBcak内层错误 错误码:{} 错误信息:{}", result_code, err_code_des);
                        order.setStatus(Constant.CANCEL);
                        order.setSettleStatus(Constant.CANCEL);
                        order.setOvertime(new Date());
                        order.update();
                    }
                    // 处理订单逻辑
                    log.info("微信手机支付回调成功订单号:{}", out_trade_no);
                    resXml = "<xml>"
                            + "<return_code><![CDATA[SUCCESS]]></return_code>"
                            + "<return_msg><![CDATA[OK]]></return_msg>"
                            + "</xml> ";
                }else {
                    resXml = "<xml>"
                            + "<return_code><![CDATA[FAIL]]></return_code>"
                            + "<return_msg><![CDATA[验签失败]]></return_msg>"
                            + "</xml> ";
                }
            } else {
                log.info("doBcak外层错误，错误码:{} 错误信息:{}", return_code, return_msg);
                resXml = "<xml>"
                        + "<return_code><![CDATA[FAIL]]></return_code>"
                        + "<return_msg><![CDATA[报文为空]]></return_msg>"
                        + "</xml> ";
            }
        } catch (Exception e) {
            log.error("微信支付回调通知失败", e);
            resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                    + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
        }

        try {
            renderText( resXml, "text/xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理商城订单
     * @param total_fee
     * @param out_trade_no
     * @param customerId
     */
    private void dealWithShoppingOrder(BigDecimal total_fee, String out_trade_no, Integer customerId) {
        ShoppingOrder order = shoppingOrderService.findFirstByColumns(Columns.create("order_no", out_trade_no));
        List<ShoppingOrderItem> orderItemList = Lists.newArrayList();
        BigDecimal order_amt = null;
        if(null != order){
            //父级订单
            order_amt = order.getPrice().multiply(new BigDecimal(100));
            orderItemList = orderItemService.findListByColumns(Columns.create("order_id", order.getId()));
        }else {
            //子级订单
            ShoppingOrderItem orderItem = orderItemService.findFirstByColumns(Columns.create("order_no", out_trade_no));
            orderItemList.add(orderItem);
            order_amt = orderItem.getPrice().multiply(new BigDecimal(100));
        }
        if (order_amt.compareTo(total_fee)==0) {
            orderItemList.forEach(e -> {
                //修改订单状态
                e.setStatus(ShoppingOrderEnum.AWAIT_SHIP.getValue());
                e.setSettleStatus(ShoppingOrderEnum.PAID.getValue());
                e.setOvertime(new Date());
                e.update();

                //是从购物车购买的订单，需清空购物车
                if("1".equals(e.getType())){
                    ShoppingCart cart = cartService.findFirstByColumns(Columns.create("item_id", e.getItemId()).eq("customer_id", customerId));
                    Db.delete("delete from t_shopping_cart where id = " + cart.getId());
                }
            });
            //商城订单返利
            shoppingCommission(order, orderItemList, out_trade_no);
        } else {
            log.info("doBcak金额不匹配 微信金额:{} 我方金额:{}", total_fee, order_amt);
            orderItemList.forEach(e -> {
                e.setStatus(ShoppingOrderEnum.CANCEL.getValue());
                e.setSettleStatus(ShoppingOrderEnum.CANCEL.getValue());
                e.setOvertime(new Date());
                e.update();
            });
        }
    }

    /**
     * 商城订单返利
     * @param order
     * @param orderItemList
     * @param out_trade_no
     */
    private void shoppingCommission(ShoppingOrder order, List<ShoppingOrderItem> orderItemList, String out_trade_no) {
        CustomerLevel level;
        CustomerInfo customer = new CustomerInfo();
        if(null != order){
            customer = customerInfoService.findById(order.getCustomerId());
        }else {
            customer = customerInfoService.findById(orderItemList.get(0).getCustomerId());
        }

        CustomerInfo pcustomer = new CustomerInfo();
        if(customer.getPid()!=null && customer.getPid() != 0) {
            pcustomer = customerInfoService.findById(customer.getPid());
            level = customerLevelService.findById(pcustomer.getLevelId());
            //一级返利
            firstShoppingRebate(out_trade_no, pcustomer, level, orderItemList);
        }
        if(pcustomer.getPid() != null && pcustomer.getPid() != 0){
            pcustomer = customerInfoService.findById(pcustomer.getPid());
            level = customerLevelService.findById(pcustomer.getLevelId());
            //二级返利
            secondRebate(out_trade_no, pcustomer, level, orderItemList);
        }
    }

    /**
     * 商城二级返利
     * @param orderNo
     * @param pcustomer
     * @param level
     * @param orderItemList
     */
    private void secondRebate(String orderNo, CustomerInfo pcustomer, CustomerLevel level, List<ShoppingOrderItem> orderItemList) {
        if(level.getTitle().equals(CustomerEnum.PARTNER.getValue())){
            //二级上级为合伙人
            BigDecimal amount = new BigDecimal(0);
            for(ShoppingOrderItem item : orderItemList){
                ItemInfo info = itemInfoService.findById(item.getItemId());
                amount = amount.add(new BigDecimal(item.getPrice().toString()).multiply(new BigDecimal(info.getSecondFranchiseeRate()).divide(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP));
            }
            savebill(pcustomer.getId(),"佣金",orderNo,"订单"+orderNo+"佣金", amount);
        }else if(level.getTitle().equals(CustomerEnum.FRANCHISEE.getValue())){
            //二级上级为加盟商
            BigDecimal amount = new BigDecimal(0);
            for(ShoppingOrderItem item : orderItemList){
                ItemInfo info = itemInfoService.findById(item.getItemId());
                amount = amount.add(new BigDecimal(item.getPrice().toString()).multiply(new BigDecimal(info.getSecondFranchiseeRate()).divide(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP));
            }
            savebill(pcustomer.getId(),"佣金",orderNo,"订单"+orderNo+"佣金", amount);
        }else {
            //递归查询当前用户最上级合伙人和加盟商
            getShoppingHighLevelInfo(pcustomer, orderNo, orderItemList);
        }
    }

    private void getShoppingHighLevelInfo(CustomerInfo pcustomer, String orderNo, List<ShoppingOrderItem> orderItemList) {
        List<Record> list = Db.find("SELECT T2.id, T2.level_id levelId FROM (SELECT @r AS _id, (SELECT @r := pid FROM customer_info WHERE id = _id) AS parent_id, @l := @l + 1 AS lvl FROM (SELECT @r := ?, @l := 0) vars, customer_info h WHERE @r <> 0) T1 JOIN customer_info T2 ON T1._id = T2.id where T2.id <> ? ORDER BY T1.lvl DESC", pcustomer.getId(), pcustomer.getId());
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        CustomerLevel partLevel = customerLevelService.findFirstByColumns(Columns.create("title", CustomerEnum.PARTNER.getValue()));
        CustomerLevel fLevel = customerLevelService.findFirstByColumns(Columns.create("title", CustomerEnum.FRANCHISEE.getValue()));
        List<CustomerInfo> partList = new ArrayList<>();
        List<CustomerInfo> fList = new ArrayList<>();
        for(Record record : list){
            if(record.getInt("levelId") == partLevel.getId()){
                CustomerInfo customerInfo = new CustomerInfo();
                customerInfo.setId(record.getInt("id"));
                partList.add(customerInfo);
            }
            if(record.getInt("levelId") == fLevel.getId()){
                CustomerInfo customerInfo = new CustomerInfo();
                customerInfo.setId(record.getInt("id"));
                fList.add(customerInfo);
            }
        }

        CustomerInfo partInfo = partList.get(0);
        CustomerInfo fInfo = fList.get(0);
        BigDecimal partAmount = new BigDecimal(0);
        BigDecimal fAmount = new BigDecimal(0);
        for(ShoppingOrderItem item : orderItemList){
            ItemInfo info = itemInfoService.findById(item.getItemId());
            partAmount = partAmount.add(new BigDecimal(item.getPrice().toString()).multiply(new BigDecimal(info.getSecondPartnerRate()).divide(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP));
            fAmount = fAmount.add(new BigDecimal(item.getPrice().toString()).multiply(new BigDecimal(info.getSecondFranchiseeRate()).divide(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP));
        }
        savebill(partInfo.getId(),"佣金",orderNo,"订单"+orderNo+"佣金", partAmount);
        savebill(fInfo.getId(),"佣金",orderNo,"订单"+orderNo+"佣金", fAmount);
    }

    /**
     * 商城一级返利
     * @param orderNo
     * @param pcustomer
     * @param level
     * @param orderItemList
     */
    private void firstShoppingRebate(String orderNo, CustomerInfo pcustomer, CustomerLevel level, List<ShoppingOrderItem> orderItemList) {
        if(level.getTitle().equals(CustomerEnum.COMMON_USER.getValue())){
            //直接上级为普通用户
            BigDecimal amount = new BigDecimal(0);
            for(ShoppingOrderItem item : orderItemList){
                ItemInfo info = itemInfoService.findById(item.getItemId());
                amount = amount.add(new BigDecimal(item.getPrice().toString()).multiply(new BigDecimal(info.getDirectRate()).divide(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP));
            }
            savebill(pcustomer.getId(),"佣金",orderNo,"订单"+orderNo+"佣金", amount);
        }else if(level.getTitle().equals(CustomerEnum.PARTNER.getValue())){
            //直接上级为合伙人
            BigDecimal amount = new BigDecimal(0);
            for(ShoppingOrderItem item : orderItemList){
                ItemInfo info = itemInfoService.findById(item.getItemId());
                amount = amount.add(new BigDecimal(item.getPrice().toString()).multiply(new BigDecimal(info.getFirstPartnerRate()).divide(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP));
            }
            savebill(pcustomer.getId(),"佣金",orderNo,"订单"+orderNo+"佣金", amount);
        }else if(level.getTitle().equals(CustomerEnum.FRANCHISEE.getValue())){
            //直接上级为加盟商
            BigDecimal amount = new BigDecimal(0);
            for(ShoppingOrderItem item : orderItemList){
                ItemInfo info = itemInfoService.findById(item.getItemId());
                amount = amount.add(new BigDecimal(item.getPrice().toString()).multiply(new BigDecimal(info.getFirstFranchiseeRate()).divide(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP));
            }
            savebill(pcustomer.getId(),"佣金",orderNo,"订单"+orderNo+"佣金", amount);
        }
    }

    private void cancel(CustomerOrder order,BigDecimal total_fee,BigDecimal order_amt) {
        log.info("doBcak金额不匹配 微信金额:{} 我方金额:{}", total_fee, order_amt);
        order.setStatus(Constant.CANCEL);
        order.setSettleStatus(Constant.CANCEL);
        order.setOvertime(new Date());
        order.update();
    }

    private void finish(CustomerOrder order,String out_trade_no) {
        order.setStatus("finish");
        order.setSettleStatus("finish");
        order.setOvertime(new Date());
        order.update();
    }

    /**
     * 微信支付回调
     */
    @ApiOperation(value = "微信支付回调", httpMethod = "POST")
    public void doAdvanceBack() {
        WxConfig config = JbootConfigManager.me().get(WxConfig.class);
        String resXml = "";
        try {
            // 解析XML
            Map<String, String> map = MobileUtil.parseXml(this.getRequest());
            log.info("------------------> doBack:"+ JSON.toJSONString(map));
            String return_code = map.get("return_code");// 状态
            String return_msg = map.get("return_msg");
            if (return_code.equals("SUCCESS")) {
                SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>(map);
                String sign = PayCommonUtil.createSign("UTF-8", packageParams,
                        config.getApiKey());
                String return_sign = map.get("sign");
                if(sign.equals(return_sign)) {
                    String result_code = map.get("result_code");
                    String err_code_des = map.get("err_code_des");
                    String out_trade_no = map.get("out_trade_no");// 订单号
                    String order_no = out_trade_no.split("_")[1];
                    //查询订单
                    CustomerOrder order = customerOrderService.findFirstByColumns(Columns.create("order_no",order_no));
                    if(result_code.equals("SUCCESS")&&order!=null) {
                        Db.update("update customer_order set status = 'service' where order_no = ?",order_no);
                    }else {
                        log.info("doBcak内层错误 错误码:{} 错误信息:{}", result_code, err_code_des);
                        order.setStatus(Constant.CANCEL);
                        order.setSettleStatus(Constant.CANCEL);
                        order.setOvertime(new Date());
                        order.update();
                    }
                    // 处理订单逻辑
                    log.info("微信手机支付回调成功订单号:{}", out_trade_no);
                    resXml = "<xml>"
                            + "<return_code><![CDATA[SUCCESS]]></return_code>"
                            + "<return_msg><![CDATA[OK]]></return_msg>"
                            + "</xml> ";
                }else {
                    resXml = "<xml>"
                            + "<return_code><![CDATA[FAIL]]></return_code>"
                            + "<return_msg><![CDATA[验签失败]]></return_msg>"
                            + "</xml> ";
                }
            } else {
                log.info("doBcak外层错误，错误码:{} 错误信息:{}", return_code, return_msg);
                resXml = "<xml>"
                        + "<return_code><![CDATA[FAIL]]></return_code>"
                        + "<return_msg><![CDATA[报文为空]]></return_msg>"
                        + "</xml> ";
            }
        } catch (Exception e) {
            log.error("微信支付回调通知失败", e);
            resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                    + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
        }

        try {
            renderText( resXml, "text/xml");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 尾款微信支付回调
     */
    @ApiOperation(value = "微信支付回调", httpMethod = "POST")
    public void doFinalBack() {
        WxConfig config = JbootConfigManager.me().get(WxConfig.class);
        String resXml = "";
        try {
            // 解析XML
            Map<String, String> map = MobileUtil.parseXml(this.getRequest());
            log.info("------------------> doBack:"+ JSON.toJSONString(map));
            String return_code = map.get("return_code");// 状态
            String return_msg = map.get("return_msg");
            if (return_code.equals("SUCCESS")) {
                SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>(map);
                String sign = PayCommonUtil.createSign("UTF-8", packageParams,
                        config.getApiKey());
                String return_sign = map.get("sign");
                if(sign.equals(return_sign)) {
                    String result_code = map.get("result_code");
                    String err_code_des = map.get("err_code_des");
                    String out_trade_no = map.get("out_trade_no");// 订单号
                    String order_no =  out_trade_no.split("_")[1];
                    //查询订单
                    CustomerOrder order = customerOrderService.findFirstByColumns(Columns.create("order_no",order_no));
                    if(result_code.equals("SUCCESS")) {
                        order.setStatus("finish");
                        order.setSettleStatus("finish");
                        order.setOvertime(new Date());
                        order.update();
                        commission(order);
                    }else {
                        log.info("doBcak内层错误 错误码:{} 错误信息:{}", result_code, err_code_des);
                        order.setStatus(Constant.CANCEL);
                        order.setSettleStatus(Constant.CANCEL);
                        order.setOvertime(new Date());
                        order.update();
                    }
                    // 处理订单逻辑
                    log.info("微信手机支付回调成功订单号:{}", out_trade_no);
                    resXml = "<xml>"
                            + "<return_code><![CDATA[SUCCESS]]></return_code>"
                            + "<return_msg><![CDATA[OK]]></return_msg>"
                            + "</xml> ";
                }else {
                    resXml = "<xml>"
                            + "<return_code><![CDATA[FAIL]]></return_code>"
                            + "<return_msg><![CDATA[验签失败]]></return_msg>"
                            + "</xml> ";
                }
            } else {
                log.info("doBcak外层错误，错误码:{} 错误信息:{}", return_code, return_msg);
                resXml = "<xml>"
                        + "<return_code><![CDATA[FAIL]]></return_code>"
                        + "<return_msg><![CDATA[报文为空]]></return_msg>"
                        + "</xml> ";
            }
        } catch (Exception e) {
            log.error("微信支付回调通知失败", e);
            resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                    + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
        }
        try {
            renderText( resXml, "text/xml");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 返利
     * @param order
     */
    private void commission(CustomerOrder order) {
        CustomerInfo customer = customerInfoService.findById(order.getCustomerId());
        ItemInfo info = itemInfoService.findById(order.getItemId());
        //0收纳 1课程
        BigDecimal order_amt = null;
        if(info.getType()==0) {
            CustomerOrderEvaluate orderEvaluate  = orderEvaluateService.findFirstByColumns(Columns.create("order_no",order.getOrderNo()));
            SysCity city =sysCityService.findFirstByColumns(Columns.create("name",order.getCity()));
            order_amt = order.getAdvanceAmount().add(order.getFinalAmount()).add(city.getPrice());
            savebill(orderEvaluate.getArrangerId(),"服务",order.getOrderNo(),"订单"+order.getOrderNo()+"服务费",order_amt.multiply(city.getOrderRate()).setScale(2,BigDecimal.ROUND_HALF_UP));
        }else if(info.getType() == 1){
            order_amt = order.getPrice();
        }
        CustomerLevel level;
        CustomerInfo pcustomer = new CustomerInfo();
        if(customer.getPid()!=null && customer.getPid() != 0) {
            pcustomer = customerInfoService.findById(customer.getPid());
            level = customerLevelService.findById(pcustomer.getLevelId());
            //一级返利
            firstRebate(pcustomer, order, level, info, order_amt);
        }
        if(pcustomer.getPid() != null && pcustomer.getPid() != 0){
            pcustomer = customerInfoService.findById(pcustomer.getPid());
            level = customerLevelService.findById(pcustomer.getLevelId());
            //二级返利
            secondRebate(pcustomer, order, level, info, order_amt);
        }
    }

    /**
     * 二级返利
     * @param pcustomer
     * @param order
     * @param level
     * @param info
     * @param order_amt
     */
    private void secondRebate(CustomerInfo pcustomer, CustomerOrder order, CustomerLevel level, ItemInfo info, BigDecimal order_amt) {
        String orderNo = order.getOrderNo();
        if(level.getTitle().equals(CustomerEnum.PARTNER.getValue())){
            //二级上级为合伙人
            savebill(pcustomer.getId(),"佣金",orderNo,"订单"+orderNo+"佣金",order_amt.multiply(new BigDecimal(info.getSecondFranchiseeRate()).divide(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP));
        }else if(level.getTitle().equals(CustomerEnum.FRANCHISEE.getValue())){
            //二级上级为加盟商
            savebill(pcustomer.getId(),"佣金",orderNo,"订单"+orderNo+"佣金",order_amt.multiply(new BigDecimal(info.getSecondFranchiseeRate()).divide(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP));
        }else {
            //递归查询当前用户最上级合伙人和加盟商
            getHighLevelInfo(pcustomer, orderNo, info, order_amt);
        }
    }

    /**
     * 递归查询当前用户最上级合伙人和加盟商
     * @param pcustomer
     * @param orderNo
     * @param info
     * @param order_amt
     */
    private void getHighLevelInfo(CustomerInfo pcustomer, String orderNo, ItemInfo info, BigDecimal order_amt) {
        List<Record> list = Db.find("SELECT T2.id, T2.level_id levelId FROM (SELECT @r AS _id, (SELECT @r := pid FROM customer_info WHERE id = _id) AS parent_id, @l := @l + 1 AS lvl FROM (SELECT @r := ?, @l := 0) vars, customer_info h WHERE @r <> 0) T1 JOIN customer_info T2 ON T1._id = T2.id where T2.id <> ? ORDER BY T1.lvl DESC", pcustomer.getId(), pcustomer.getId());
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        CustomerLevel partLevel = customerLevelService.findFirstByColumns(Columns.create("title", CustomerEnum.PARTNER.getValue()));
        CustomerLevel fLevel = customerLevelService.findFirstByColumns(Columns.create("title", CustomerEnum.FRANCHISEE.getValue()));
        List<CustomerInfo> partList = new ArrayList<>();
        List<CustomerInfo> fList = new ArrayList<>();
        for(Record record : list){
            if(record.getInt("levelId") == partLevel.getId()){
                CustomerInfo customerInfo = new CustomerInfo();
                customerInfo.setId(record.getInt("id"));
                partList.add(customerInfo);
            }
            if(record.getInt("levelId") == fLevel.getId()){
                CustomerInfo customerInfo = new CustomerInfo();
                customerInfo.setId(record.getInt("id"));
                fList.add(customerInfo);
            }
        }

        if(CollectionUtils.isNotEmpty(partList)){
            CustomerInfo partInfo = partList.get(0);
            savebill(partInfo.getId(),"佣金",orderNo,"订单"+orderNo+"佣金",order_amt.multiply(new BigDecimal(info.getSecondPartnerRate()).divide(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP));
        }

        if(CollectionUtils.isNotEmpty(fList)){
            CustomerInfo fInfo = fList.get(0);
            savebill(fInfo.getId(),"佣金",orderNo,"订单"+orderNo+"佣金",order_amt.multiply(new BigDecimal(info.getSecondFranchiseeRate()).divide(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP));
        }
    }

    /**
     * 一级返利
     * @param pcustomer
     * @param order
     * @param level
     * @param info
     * @param order_amt
     */
    private void firstRebate(CustomerInfo pcustomer, CustomerOrder order, CustomerLevel level, ItemInfo info,
                             BigDecimal order_amt) {
        String orderNo = order.getOrderNo();
        if(level.getTitle().equals(CustomerEnum.COMMON_USER.getValue())){
            //直接上级为普通用户
            savebill(pcustomer.getId(),"佣金",orderNo,"订单"+orderNo+"佣金",order_amt.multiply(new BigDecimal(info.getDirectRate()).divide(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP));
        }else if(level.getTitle().equals(CustomerEnum.PARTNER.getValue())){
            //直接上级为合伙人
            savebill(pcustomer.getId(),"佣金",orderNo,"订单"+orderNo+"佣金",order_amt.multiply(new BigDecimal(info.getFirstPartnerRate()).divide(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP));
        }else if(level.getTitle().equals(CustomerEnum.FRANCHISEE.getValue())){
            //直接上级为加盟商
            savebill(pcustomer.getId(),"佣金",orderNo,"订单"+orderNo+"佣金",order_amt.multiply(new BigDecimal(info.getFirstFranchiseeRate()).divide(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_HALF_UP));
        }
    }


    private void savebill(Integer customerId,String type,String orderNo,String content,BigDecimal amount){
        CustomerInfo customer = customerInfoService.findById(customerId);
        if(customer.getVersion()!=null) {
            for(;;){
                if(customer.getVersion() == 1){
                    customer.setVersion(2);
                    customer.update();
                    customer.setBalance(customer.getBalance().add(amount));
                    customer.setVersion(1);
                    customer.update();
                    break;
                }
            }
        }else {
            customer.setVersion(2);
            customer.update();
            customer.setBalance(customer.getBalance().add(amount));
            customer.setVersion(1);
            customer.update();
        }
        CustomerBill bill = new CustomerBill();
        bill.setCustomerId(customerId);
        bill.setType(type);
        bill.setOrderNo(orderNo);
        bill.setContent(content);
        bill.setAmount(amount);
        bill.setBalance(customer.getBalance());
        bill.setAddtime(new Date());
        bill.setStatus("1");
        bill.save();

        if(type.equals("佣金")) {
            CustomerCommission  commission  = new CustomerCommission();
            commission.setAmount(amount);
            commission.setCommissionTime(new Date());
            commission.setCustomerId(new Long(customerId));
            commission.setLevel(new Long(customer.getLevelId()));
            commission.setCommissionType("佣金");
            commission.save();
        }


    }
}
