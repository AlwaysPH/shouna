package com.qcws.shouna.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SMSUtil {

    private static final Logger log = LoggerFactory.getLogger(SMSUtil.class);

    public static void main(String[] args) {
        String phone = "15274915850";
        String url = "http://dysmsapi.aliyuncs.com/?Signature=";
        String accessKeyId = "LTAI5t93uaHWJeGCU5G4SRG1";
        String accessSecret = "dBKsD9Tydw59fsU58rTzAds4It4OqK";
        String templateCode = "SMS_219741801";
        send(phone, url, accessKeyId, accessSecret, templateCode);
    }

    public static void send(String phone, String url, String accessKeyId, String accessSecret, String templateCode){
        try {
            String sortStr = getSortQueryStringTmp(accessKeyId, templateCode, phone);
            String sign = getSign(sortStr, accessSecret);
            Map<String, Object> map = HttpUtil.httpGet(url + sign + sortStr);
            if(null != map){
                if(map.get("Code").toString().equals("OK")){
                    log.info("短信发送成功");
                }
            }
        } catch (Exception e) {
            log.error("发送短信失败", e);
        }
    }

    private static String getSign(String sortStr, String accessSecret) throws Exception {
        // 去除第一个多余的&符号
        String sortedQueryString = sortStr.substring(1);
        StringBuilder stringToSign = new StringBuilder();
        stringToSign.append("GET").append("&");
        stringToSign.append(specialUrlEncode("/")).append("&");
        stringToSign.append(specialUrlEncode(sortedQueryString));
        String sign = sign(accessSecret + "&", stringToSign.toString());
        // 签名最后也要做特殊URL编码
        return specialUrlEncode(sign);
    }

    private static String getSortQueryStringTmp(String accessKeyId, String templateCode, String phone) throws Exception {
        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(new java.util.SimpleTimeZone(0, "GMT"));// 这里一定要设置GMT时区
        java.util.Map<String, String> paras = new java.util.HashMap<String, String>();
        // 1. 系统参数
        paras.put("SignatureMethod", "HMAC-SHA1");
        paras.put("SignatureNonce", java.util.UUID.randomUUID().toString());
        paras.put("AccessKeyId", accessKeyId);
        paras.put("SignatureVersion", "1.0");
        paras.put("Timestamp", df.format(new java.util.Date()));
        paras.put("Format", "JSON");
        // 2. 业务API参数
        paras.put("Action", "SendSms");
        paras.put("Version", "2017-05-25");
        paras.put("RegionId", "cn-changsha");
        paras.put("PhoneNumbers", phone);
        paras.put("SignName", "整么样行动文化");
        paras.put("TemplateCode", templateCode);
        // 3. 去除签名关键字Key
        if (paras.containsKey("Signature"))
            paras.remove("Signature");
        // 4. 参数KEY排序
        java.util.TreeMap<String, String> sortParas = new java.util.TreeMap<String, String>();
        sortParas.putAll(paras);
        // 5. 构造待签名的字符串
        java.util.Iterator<String> it = sortParas.keySet().iterator();
        StringBuilder sortQueryStringTmp = new StringBuilder();
        while (it.hasNext()) {
            String key = it.next();
            sortQueryStringTmp.append("&").append(specialUrlEncode(key)).append("=").append(specialUrlEncode(paras.get(key)));
        }
        return sortQueryStringTmp.toString();
    }

    public static String specialUrlEncode(String value) throws Exception {
        return java.net.URLEncoder.encode(value, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }

    public static String sign(String accessSecret, String stringToSign) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
        mac.init(new javax.crypto.spec.SecretKeySpec(accessSecret.getBytes("UTF-8"), "HmacSHA1"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        return new sun.misc.BASE64Encoder().encode(signData);
    }
}
