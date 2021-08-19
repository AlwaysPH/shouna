package com.qcws.shouna.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import com.google.common.collect.Lists;
import com.qcws.shouna.model.*;
import com.qcws.shouna.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Inject;
import com.qcws.shouna.dto.api.WxPayDto;
import com.qcws.shouna.dto.api.WxPayResDto;
import com.qcws.shouna.utils.Constant;
import com.qcws.shouna.utils.wx.DateUtil;
import com.qcws.shouna.utils.wx.HttpUtil;
import com.qcws.shouna.utils.wx.MobileUtil;
import com.qcws.shouna.utils.wx.PayCommonUtil;
import com.qcws.shouna.utils.wx.XMLUtil;

import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import lombok.extern.slf4j.Slf4j;
import org.jdom.JDOMException;

@Bean
@Slf4j
public class WxPayServiceImpl  implements WxPayService {

    // 微信支付统一接口(POST)
    public final static String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    @Inject CustomerOrderService customerOrderService;
    @Inject SysCityService sysCityService;
    @Inject ItemInfoService itemInfoService;

    @Inject
    ShoppingOrderService shoppingOrderService;

    @Inject
    private ShoppingOrderItemService orderItemService;

    @Override
    public WxPayResDto dopay(WxPayDto wxDoPayDto, String ip) throws Exception {
        WxPayResDto wxDoPayResDto = new WxPayResDto();
        String orderNo = wxDoPayDto.getOrderNo();
        CustomerOrder order = customerOrderService.findFirstByColumns(Columns.create("order_no",orderNo));

        if(!order.getSettleStatus().equals(Constant.UNDONE)) {
            wxDoPayResDto.setCode("FAIL");
            wxDoPayResDto.setMsg("该笔订单已支付");
            return wxDoPayResDto;
        }
        BigDecimal pay_amt;
        ItemInfo  info = itemInfoService.findById(order.getItemId());
        
        if(info.getType()==0) {
        	 SysCity sysCity =sysCityService.findFirstByColumns(Columns.create("english_name",order.getCity()));
             pay_amt = sysCity.getPrice();
        }else {
        	 pay_amt = order.getPrice();
        } 
        //BigDecimal pay_amt = new BigDecimal("0.01");
        BigDecimal wx_amt = pay_amt.multiply(new BigDecimal(100));
        String totalFee = String.valueOf(wx_amt.intValue());//单位分
        // 获取code 这个在微信支付调用时会自动加上这个参数 无须设置
        String code = wxDoPayDto.getCode();
        // 获取用户openID(JSAPI支付必须传openid)
        String openId = MobileUtil.getOpenId(code, wxDoPayDto.getAppId(), wxDoPayDto.getAppSecret());
        if(StringUtils.isEmpty(openId)) {
            wxDoPayResDto.setCode("FAIL");
            wxDoPayResDto.setMsg("openId获取失败");
            return wxDoPayResDto;
        }
        String notify_url = wxDoPayDto.getServerUrl() + "/api/payment/doBack";// 回调接口
        String trade_type = "JSAPI";// 交易类型H5支付 也可以是小程序支付参数
        SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
        this.commonParams(packageParams, wxDoPayDto);
        packageParams.put("appid",wxDoPayDto.getAppId());
        packageParams.put("body", order.getItemName());// 商品描述
        packageParams.put("mch_id", wxDoPayDto.getMerId());// 商品描述
        packageParams.put("out_trade_no", orderNo);// 商户订单号
        packageParams.put("total_fee", totalFee);// 总金额
        packageParams.put("spbill_create_ip", ip);// 发起人IP地址
        packageParams.put("notify_url", notify_url);// 回调地址
        packageParams.put("trade_type", trade_type);// 交易类型
        packageParams.put("openid", openId);// 用户openID
        String sign = PayCommonUtil.createSign("UTF-8", packageParams,
                wxDoPayDto.getApiKey());
        packageParams.put("sign", sign);// 签名
        String requestXML = PayCommonUtil.getRequestXml(packageParams);
        //String requestXML ="<xml><appid>wx7b32a6c037b19592</appid><body>报告</body><mch_id>1561494081</mch_id><nonce_str>1724399120</nonce_str><notify_url>https://b.7liv.com/wxpay/doBack</notify_url><openid>oAnuB4hnYMNbu5VDn-MYG6uSWmjI</openid><out_trade_no>test_20191103_10001</out_trade_no><sign>BEFEACA213C28C47A41D7630DE7B207E</sign><spbill_create_ip>127.0.0.1</spbill_create_ip><total_fee>1</total_fee><trade_type>JSAPI</trade_type></xml>";
        String resXml = HttpUtil.postData(UNIFIED_ORDER_URL,
                requestXML);
        Map<String, String> map = XMLUtil.doXMLParse(resXml);
        String returnCode = map.get("return_code");
        String returnMsg = map.get("return_msg");
        if ("SUCCESS".equals(returnCode)) {
            String resultCode = map.get("result_code");
            String errCodeDes = map.get("err_code_des");
            if ("SUCCESS".equals(resultCode)) {
                // 获取预支付交易会话标识
                String prepay_id = map.get("prepay_id");
                String prepay_id2 = "prepay_id=" + prepay_id;
                String packages = prepay_id2;
                SortedMap<Object, Object> finalpackage = new TreeMap<Object, Object>();
                String timestamp = DateUtil.getTimestamp();
                String nonceStr = packageParams.get("nonce_str").toString();
                finalpackage.put("appId", wxDoPayDto.getAppId());
                finalpackage.put("timeStamp", timestamp);
                finalpackage.put("nonceStr", nonceStr);
                finalpackage.put("package", packages);
                finalpackage.put("signType", "MD5");
                // 这里很重要 参数一定要正确 狗日的腾讯 参数到这里就成大写了
                // 可能报错信息(支付验证签名失败 get_brand_wcpay_request:fail)
                sign = PayCommonUtil.createSign("UTF-8", finalpackage,
                        wxDoPayDto.getApiKey());
                wxDoPayResDto.setTimeStamp(timestamp);
                wxDoPayResDto.setNonceStr(nonceStr);
                wxDoPayResDto.setPackages(packages);
                wxDoPayResDto.setSignType("MD5");
                wxDoPayResDto.setPaySign(sign);

                wxDoPayResDto.setAmount(pay_amt);
                wxDoPayResDto.setCode("SUCCESS");
                wxDoPayResDto.setMsg("成功");
            } else {
                log.info("内层错误，订单号:{} 错误信息:{}", orderNo, errCodeDes);
                wxDoPayResDto.setCode(resultCode);
                wxDoPayResDto.setMsg(errCodeDes);
            }
        } else {
            log.info("外层错误，订单号:{} 错误信息:{}", orderNo, returnMsg);
            wxDoPayResDto.setCode(returnCode);
            wxDoPayResDto.setMsg(returnMsg);
        }
        return wxDoPayResDto;
    }
    
   
    public String doQrCode(WxPayDto wxDoPayDto,BigDecimal payAmt,String no,String notifyUrl, String ip) throws Exception {
        String orderNo = wxDoPayDto.getOrderNo();
        CustomerOrder order = customerOrderService.findFirstByColumns(Columns.create("order_no",orderNo));
        BigDecimal wx_amt = payAmt.multiply(new BigDecimal(100));
        String totalFee = String.valueOf(wx_amt.intValue());//单位分
        
        String notify_url = wxDoPayDto.getServerUrl() + notifyUrl;// 回调接口
        String trade_type = "NATIVE";// 交易类型H5支付 也可以是小程序支付参数
        SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
        this.commonParams(packageParams, wxDoPayDto);
        packageParams.put("appid",wxDoPayDto.getAppId());
        packageParams.put("body", order.getItemName());// 商品描述
        packageParams.put("mch_id", wxDoPayDto.getMerId());// 商品描述
        Random rand = new Random();
        
        packageParams.put("out_trade_no",no+orderNo+"_"+rand.nextInt(100));// 商户订单号
        packageParams.put("total_fee", totalFee);// 总金额
        packageParams.put("spbill_create_ip", ip);// 发起人IP地址
        packageParams.put("notify_url", notify_url);// 回调地址
        packageParams.put("trade_type", trade_type);// 交易类型
       
        String sign = PayCommonUtil.createSign("UTF-8", packageParams,wxDoPayDto.getApiKey());
        packageParams.put("sign", sign);// 签名
        String requestXML = PayCommonUtil.getRequestXml(packageParams);
        
        
        String resXml = HttpUtil.postData(UNIFIED_ORDER_URL, requestXML);
        System.out.println(resXml);
        Map<String, String> map = XMLUtil.doXMLParse(resXml);
        return map.get("code_url");
    }

    @Override
    public WxPayResDto doShoppingPay(WxPayDto wxDoPayDto, String ip) throws JDOMException, IOException {
        WxPayResDto wxDoPayResDto = new WxPayResDto();
        //直接下单或购物车下单，orderNo为父级订单号，若从待支付订单列表下单，则为子订单号
        String orderNo = wxDoPayDto.getOrderNo();
        ShoppingOrder order = shoppingOrderService.findFirstByColumns(Columns.create("order_no",orderNo));
        List<ShoppingOrderItem> list = Lists.newArrayList();
        if(null == order){
            //从待支付订单列表支付
            list = orderItemService.findListByColumns(Columns.create("order_no", orderNo));
        }else {
            list = orderItemService.findListByColumns(Columns.create("order_id", order.getId()));
        }
        if(CollectionUtils.isEmpty(list)){
            return wxDoPayResDto;
        }
        ShoppingOrderItem orderItem = list.get(0);
        ItemInfo  itemInfo = itemInfoService.findById(orderItem.getItemId());
        if(!orderItem.getSettleStatus().equals(ShoppingOrderEnum.UN_PAY.getValue())) {
            wxDoPayResDto.setCode("FAIL");
            wxDoPayResDto.setMsg("该笔订单已支付");
            return wxDoPayResDto;
        }
        BigDecimal pay_amt = new BigDecimal(0);
        for(ShoppingOrderItem item : list){
            pay_amt = pay_amt.add(new BigDecimal(item.getPrice().toString()));
        }
        //BigDecimal pay_amt = new BigDecimal("0.01");
        BigDecimal wx_amt = pay_amt.multiply(new BigDecimal(100));
        String totalFee = String.valueOf(wx_amt.intValue());//单位分
        // 获取code 这个在微信支付调用时会自动加上这个参数 无须设置
        String code = wxDoPayDto.getCode();
        // 获取用户openID(JSAPI支付必须传openid)
        String openId = MobileUtil.getOpenId(code, wxDoPayDto.getAppId(), wxDoPayDto.getAppSecret());
        if(StringUtils.isEmpty(openId)) {
            wxDoPayResDto.setCode("FAIL");
            wxDoPayResDto.setMsg("openId获取失败");
            return wxDoPayResDto;
        }
        String notify_url = wxDoPayDto.getServerUrl() + "/api/payment/doBack";// 回调接口
        String trade_type = "JSAPI";// 交易类型H5支付 也可以是小程序支付参数
        SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
        this.commonParams(packageParams, wxDoPayDto);
        packageParams.put("appid",wxDoPayDto.getAppId());
        packageParams.put("body", itemInfo.getName());// 商品描述
        packageParams.put("mch_id", wxDoPayDto.getMerId());// 商品描述
        packageParams.put("out_trade_no", orderNo);// 商户订单号
        packageParams.put("total_fee", totalFee);// 总金额
        packageParams.put("spbill_create_ip", ip);// 发起人IP地址
        packageParams.put("notify_url", notify_url);// 回调地址
        packageParams.put("trade_type", trade_type);// 交易类型
        packageParams.put("openid", openId);// 用户openID
        String sign = PayCommonUtil.createSign("UTF-8", packageParams,
                wxDoPayDto.getApiKey());
        packageParams.put("sign", sign);// 签名
        String requestXML = PayCommonUtil.getRequestXml(packageParams);
        //String requestXML ="<xml><appid>wx7b32a6c037b19592</appid><body>报告</body><mch_id>1561494081</mch_id><nonce_str>1724399120</nonce_str><notify_url>https://b.7liv.com/wxpay/doBack</notify_url><openid>oAnuB4hnYMNbu5VDn-MYG6uSWmjI</openid><out_trade_no>test_20191103_10001</out_trade_no><sign>BEFEACA213C28C47A41D7630DE7B207E</sign><spbill_create_ip>127.0.0.1</spbill_create_ip><total_fee>1</total_fee><trade_type>JSAPI</trade_type></xml>";
        String resXml = HttpUtil.postData(UNIFIED_ORDER_URL,
                requestXML);
        Map<String, String> map = XMLUtil.doXMLParse(resXml);
        String returnCode = map.get("return_code");
        String returnMsg = map.get("return_msg");
        if ("SUCCESS".equals(returnCode)) {
            String resultCode = map.get("result_code");
            String errCodeDes = map.get("err_code_des");
            if ("SUCCESS".equals(resultCode)) {
                // 获取预支付交易会话标识
                String prepay_id = map.get("prepay_id");
                String prepay_id2 = "prepay_id=" + prepay_id;
                String packages = prepay_id2;
                SortedMap<Object, Object> finalpackage = new TreeMap<Object, Object>();
                String timestamp = DateUtil.getTimestamp();
                String nonceStr = packageParams.get("nonce_str").toString();
                finalpackage.put("appId", wxDoPayDto.getAppId());
                finalpackage.put("timeStamp", timestamp);
                finalpackage.put("nonceStr", nonceStr);
                finalpackage.put("package", packages);
                finalpackage.put("signType", "MD5");
                // 这里很重要 参数一定要正确 狗日的腾讯 参数到这里就成大写了
                // 可能报错信息(支付验证签名失败 get_brand_wcpay_request:fail)
                sign = PayCommonUtil.createSign("UTF-8", finalpackage,
                        wxDoPayDto.getApiKey());
                wxDoPayResDto.setTimeStamp(timestamp);
                wxDoPayResDto.setNonceStr(nonceStr);
                wxDoPayResDto.setPackages(packages);
                wxDoPayResDto.setSignType("MD5");
                wxDoPayResDto.setPaySign(sign);

                wxDoPayResDto.setAmount(pay_amt);
                wxDoPayResDto.setCode("SUCCESS");
                wxDoPayResDto.setMsg("成功");
            } else {
                log.info("内层错误，订单号:{} 错误信息:{}", orderNo, errCodeDes);
                wxDoPayResDto.setCode(resultCode);
                wxDoPayResDto.setMsg(errCodeDes);
            }
        } else {
            log.info("外层错误，订单号:{} 错误信息:{}", orderNo, returnMsg);
            wxDoPayResDto.setCode(returnCode);
            wxDoPayResDto.setMsg(returnMsg);
        }
        return wxDoPayResDto;
    }

    /**
     * 基础参数
     */
    public void commonParams(SortedMap<Object, Object> packageParams, WxPayDto wxDoPayDto) {
        // 账号信息
        String appid = wxDoPayDto.getAppId(); // appid
        String mch_id = wxDoPayDto.getMerId(); // 商户号
        // 生成随机字符串
        String currTime = PayCommonUtil.getCurrTime();
        String strTime = currTime.substring(8, currTime.length());
        String strRandom = PayCommonUtil.buildRandom(4) + "";
        String nonce_str = strTime + strRandom;
        packageParams.put("appid", appid);// 公众账号ID
        packageParams.put("mch_id", mch_id);// 商户号
        packageParams.put("nonce_str", nonce_str);// 随机字符串
    }
}