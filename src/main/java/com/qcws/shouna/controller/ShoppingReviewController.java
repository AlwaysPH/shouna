package com.qcws.shouna.controller;

import com.jfinal.aop.Inject;
import com.qcws.shouna.config.WxConfig;
import com.qcws.shouna.dto.DbPageSearch;
import com.qcws.shouna.dto.MessageBox;
import com.qcws.shouna.model.*;
import com.qcws.shouna.service.ShoppingOrderItemService;
import com.qcws.shouna.service.ShoppingOrderService;
import com.qcws.shouna.service.ShoppingReviewService;
import com.qcws.shouna.shiro.ShiroUtils;
import com.qcws.shouna.shiro.UserDetail;
import com.qcws.shouna.utils.wx.HttpsUtil;
import com.qcws.shouna.utils.wx.PayCommonUtil;
import com.qcws.shouna.utils.wx.XMLUtil;
import io.jboot.app.config.JbootConfigManager;
import io.jboot.db.model.Columns;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jdom.JDOMException;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 退款审核
 */
@RequiresPermissions("shopping_review")
@RequestMapping("/admin/shopping/review")
@Slf4j
public class ShoppingReviewController extends JbootController {

    private static final String REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";

    @Inject
    private ShoppingReviewService reviewService;

    @Inject
    private ShoppingOrderService orderService;

    @Inject
    private ShoppingOrderItemService orderItemService;

    public void index(){
        set("cd",reviewService.findAll());
    }

    public void grid(){
        DbPageSearch dbPageSearch = DbPageSearch.instance(this);
        dbPageSearch.select("select tr.id AS id, ci.nickname, co.order_no AS orderNo, s.content AS color, c.content AS size , tr.reason, tr.addtime AS refundAddTime, tr.overtime AS refundOverTime, tr.ex_no AS exNo, tr.refund_result AS refundResult , i.NAME AS itemName, co.telphone AS telphone, co.item_number AS itemNumber, co.price AS price, co.`status` AS status");
        dbPageSearch.from("from t_refund_info tr LEFT JOIN t_shop_order_item co ON tr.order_no = co.order_no LEFT JOIN customer_info ci ON co.customer_id " +
                "= ci.id LEFT JOIN item_info i on co.item_id = i.id" +
                " LEFT JOIN sys_config s on i.color_id = s.id LEFT JOIN sys_config c on i.size_id = c.id");
        dbPageSearch.addExcept(" co.settle_status= ?", DbPageSearch.EXIST_VAL, ShoppingOrderEnum.PAID.getValue());
        dbPageSearch.addExcept(" and (co.status= ?", DbPageSearch.EXIST_VAL, ShoppingOrderEnum.REFUND_ORDER.getValue());
        dbPageSearch.addExcept(" or co.status= ?)", DbPageSearch.EXIST_VAL, ShoppingOrderEnum.PENDING_REFUND.getValue());
        dbPageSearch.addExcept(" and (co.telphone like ?", DbPageSearch.ALLLIKE);
        dbPageSearch.addExcept("or co.order_no = ?");
        dbPageSearch.addExcept("or i.name like ?", DbPageSearch.ALLLIKE);
        dbPageSearch.addExcept("or tr.addtime like ?", DbPageSearch.ALLLIKE);
        dbPageSearch.addExcept("or ci.nickname like ?)", DbPageSearch.ALLLIKE);
        dbPageSearch.orderBy("tr.addtime desc");
        renderJson(dbPageSearch.toDataGrid());
    }

    public void audit(){
        int id = getParaToInt();
        set("c",reviewService.findById(id));
    }

    public void save(){
        ShoppingReview review = getModel(ShoppingReview.class,"i");
        ShoppingReview sr = reviewService.findById(review.getId());
        if(null != sr.getResult()){
            if(sr.getResult().equals(ShoppingOrderEnum.AGREE.getValue())){
                renderHtml(new MessageBox(false).toString());
                return;
            }
        }
        ShoppingOrderItem orderItem = orderItemService.findFirstByColumns(Columns.create("order_no", sr.getOrderNo()));
        if(null != orderItem){
            String type = review.getResult();
            if(type.equals(ShoppingOrderEnum.AGREE.getValue())){
                orderItem.setStatus(ShoppingOrderEnum.PENDING_REFUND.getValue());
            }else{
                orderItem.setStatus(ShoppingOrderEnum.REFUND_FAILED.getValue());
            }
            orderItem.saveOrUpdate();
            review.setOvertime(new Date());
            boolean bool = review.saveOrUpdate();
            renderHtml(new MessageBox(bool).toString());
        }else {
            renderHtml(new MessageBox(false).toString());
        }
    }

    /**
     * 微信退款
     * @throws JDOMException
     * @throws IOException
     */
    public void refund() throws JDOMException, IOException {
        Integer id = getParaToInt("id");
        ShoppingReview review = reviewService.findById(id);
        if(!review.getResult().equals(ShoppingOrderEnum.AGREE.getValue())){
            renderHtml(new MessageBox(false).toString());
            return;
        }
        ShoppingOrderItem orderItem = orderItemService.findFirstByColumns(Columns.create("order_no", review.getOrderNo()));
        if(!orderItem.getStatus().equals(ShoppingOrderEnum.PENDING_REFUND.getValue())){
            renderHtml(new MessageBox(false).toString());
            return;
        }
        //对接微信退款
        WxConfig config = JbootConfigManager.me().get(WxConfig.class);
        SortedMap<Object, Object> paramMap = getParam(orderItem, review, config);
        String requestXML = PayCommonUtil.getRequestXml(paramMap);
        String resXml = HttpsUtil.sendPost(REFUND_URL, requestXML, config.getMerchantId());
        Map<String, String> map = XMLUtil.doXMLParse(resXml);
        String returnCode = map.get("return_code");
        String returnMsg = map.get("return_msg");
        if ("SUCCESS".equals(returnCode)) {
            String resultCode = map.get("result_code");
            String errCodeDes = map.get("err_code_des");
            if ("SUCCESS".equals(resultCode)) {
                log.info("退款成功，订单号:{}", orderItem.getOrderNo());
                review.setOvertime(new Date());

                orderItem.setStatus(ShoppingOrderEnum.REFUND_SUCCESS.getValue());
                orderItem.setOvertime(new Date());
                orderItem.saveOrUpdate();
                renderHtml(new MessageBox(review.saveOrUpdate()).toString());
                return;
            }else {
                log.info("退款失败，订单号:{} 错误信息:{}", orderItem.getOrderNo(), errCodeDes);
                orderItem.setStatus(ShoppingOrderEnum.REFUND_FAILED.getValue());
                orderItem.setOvertime(new Date());
                orderItem.saveOrUpdate();
            }
        }else {
            log.info("退款失败，订单号:{} 错误信息:{}", orderItem.getOrderNo(), returnMsg);
            orderItem.setStatus(ShoppingOrderEnum.REFUND_FAILED.getValue());
            orderItem.setOvertime(new Date());
            orderItem.saveOrUpdate();
        }
        renderHtml(new MessageBox(false).toString());
    }

    private SortedMap<Object, Object> getParam(ShoppingOrderItem orderItem, ShoppingReview review, WxConfig config) {
        SortedMap<Object, Object> paramMap = new TreeMap<Object, Object>();
        //商户账号appid
        paramMap.put("mch_appid", config.getAppId());
        //商户号
        paramMap.put("mchid", config.getMerchantId());
        //商户订单号
        paramMap.put("transaction_id", orderItem.getOrderNo());
        //商户退款单号
        paramMap.put("out_refund_no", orderItem.getId());
        String currTime = PayCommonUtil.getCurrTime();
        String strTime = currTime.substring(8, currTime.length());
        String strRandom = PayCommonUtil.buildRandom(4) + "";
        //随机字符串
        paramMap.put("nonce_str", strTime + strRandom);
        paramMap.put("total_fee", orderItem.getPrice().intValue() * 100);
        paramMap.put("refund_fee", orderItem.getPrice().intValue() * 100);
        paramMap.put("refund_desc", review.getReason());
        String sign = PayCommonUtil.createSign("UTF-8", paramMap,
                config.getApiKey());
        paramMap.put("sign", sign);
        return paramMap;
    }
}
