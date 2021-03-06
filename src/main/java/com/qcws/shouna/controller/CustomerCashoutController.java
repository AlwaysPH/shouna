package com.qcws.shouna.controller;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import com.qcws.shouna.config.WxConfig;
import com.qcws.shouna.dto.MessageBox;
import com.qcws.shouna.dto.PageSearch;
import com.qcws.shouna.dto.SimpleDatagrid;
import com.qcws.shouna.model.CustomerCashout;
import com.qcws.shouna.model.CustomerInfo;
import com.qcws.shouna.service.CustomerCashoutService;

import com.qcws.shouna.service.CustomerInfoService;
import com.qcws.shouna.utils.wx.HttpUtil;
import com.qcws.shouna.utils.wx.HttpsUtil;
import com.qcws.shouna.utils.wx.PayCommonUtil;
import com.qcws.shouna.utils.wx.XMLUtil;
import io.jboot.app.config.JbootConfigManager;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import lombok.extern.slf4j.Slf4j;
import org.jdom.JDOMException;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Slf4j
@RequestMapping("/admin/customer/cashapply")
public class CustomerCashoutController extends JbootController {

    public final static String WITHDRAW_URL = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";

	@Inject
    CustomerCashoutService cashoutService;

	@Inject
    CustomerInfoService customerInfoService;
	
	public void index(){

    }

    public void grid(){
        PageSearch pageSearch = PageSearch.instance(this);
        Page<CustomerCashout> page = cashoutService.paginateByColumns(
                pageSearch.getPageNumber(),
                pageSearch.getPageSize(),
                pageSearch.getColumns("account_name","bank_name","card_no","amount","status",
                		"addtime","overtime","pay_amount","pay_no"),
                pageSearch.getOrderBy()
        );
        renderJson(new SimpleDatagrid(page));
    }
    
    
    public void cashover() throws JDOMException, IOException {
        int id = getParaToInt();
        CustomerCashout cashout = cashoutService.findById(id);
        if(null == cashout){
            renderHtml(new MessageBox(false).toString());
            return;
        }
        CustomerInfo info = customerInfoService.findById(cashout.getCustomerId());
        if(null == info){
            renderHtml(new MessageBox(false).toString());
            return;
        }
        WxConfig config = JbootConfigManager.me().get(WxConfig.class);
        SortedMap<Object, Object> paramMap = getParam(cashout, info, config);
        String requestXML = PayCommonUtil.getRequestXml(paramMap);
        String resXml = HttpsUtil.sendPost(WITHDRAW_URL, requestXML, config.getMerchantId());
        Map<String, String> map = XMLUtil.doXMLParse(resXml);
        String returnCode = map.get("return_code");
        String returnMsg = map.get("return_msg");
        if ("SUCCESS".equals(returnCode)) {
            String resultCode = map.get("result_code");
            String errCodeDes = map.get("err_code_des");
            if ("SUCCESS".equals(resultCode)) {
                log.info("????????????????????????:{}", cashout.getOrderNo());
                cashout.setId(id);
                cashout.setStatus("finish");
                renderHtml(new MessageBox(cashout.saveOrUpdate()).toString());
                return;
            }else {
                log.info("????????????????????????:{} ????????????:{}", cashout.getOrderNo(), errCodeDes);
            }
        }else {
            log.info("????????????????????????:{} ????????????:{}", cashout.getOrderNo(), returnMsg);
        }
        renderHtml(new MessageBox(false).toString());
    }

    private SortedMap<Object, Object> getParam(CustomerCashout cashout, CustomerInfo info, WxConfig config) {
        SortedMap<Object, Object> paramMap = new TreeMap<Object, Object>();
        //????????????appid
        paramMap.put("mch_appid", config.getAppId());
        //?????????
        paramMap.put("mchid", config.getMerchantId());
        //???????????????
        paramMap.put("partner_trade_no", cashout.getOrderNo());
        //?????????????????????
        paramMap.put("check_name", "NO_CHECK");
        String currTime = PayCommonUtil.getCurrTime();
        String strTime = currTime.substring(8, currTime.length());
        String strRandom = PayCommonUtil.buildRandom(4) + "";
        //???????????????
        paramMap.put("nonce_str", strTime + strRandom);
        paramMap.put("amount", cashout.getAmount().intValue());
        paramMap.put("desc", "??????");
        paramMap.put("openid", info.getOpenid());
        String sign = PayCommonUtil.createSign("UTF-8", paramMap,
                config.getApiKey());
        paramMap.put("sign", sign);
        return paramMap;
	}

}
