package com.qcws.shouna.service;

import java.io.IOException;
import java.math.BigDecimal;

import com.qcws.shouna.dto.api.WxPayDto;
import com.qcws.shouna.dto.api.WxPayResDto;
import org.jdom.JDOMException;

public interface WxPayService {

    /**
     * 发起微信支付
     * @param wxPayDto
     * @return
     * @throws Exception
     */
    public WxPayResDto dopay(WxPayDto wxPayDto,String ip) throws Exception;

    public String doQrCode(WxPayDto wxDoPayDto,BigDecimal payAmt,String no,String notifyUrl, String ip) throws Exception;

    WxPayResDto doShoppingPay(WxPayDto wxPayDto, String ipAddress) throws JDOMException, IOException;
}
