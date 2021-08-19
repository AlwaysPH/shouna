package com.qcws.shouna.utils.wx;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.*;

@Slf4j
public class HttpsUtil {

    /**
     * 微信提现接口
     * @param url
     * @param postXml
     * @param mchId
     * @return
     */
    public static String sendPost(String url, String postXml, String mchId){
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        String result = null;
        try {
            //获取微信退款证书
            KeyStore keyStore = WxUtil.getCertificate(mchId);
            SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(keyStore, mchId.toCharArray()).build();
            SSLConnectionSocketFactory sslf = new SSLConnectionSocketFactory(sslContext);
            httpClient = HttpClients.custom().setSSLSocketFactory(sslf).build();
            HttpPost httpPost = new HttpPost(url);

            StringEntity reqEntity = new StringEntity(postXml);
            // 设置类型
            reqEntity.setContentType("application/x-www-form-urlencoded");
            httpPost.setEntity(reqEntity);
            httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (null != httpEntity) {
                result = EntityUtils.toString(httpEntity, "UTF-8");
                EntityUtils.consume(httpEntity);
            }
            return result;
        } catch (Exception e) {
            log.error("调用微信提现、退款接口失败", e);
        } finally {//关流
            try {
                if(null != httpClient){
                    httpClient.close();
                }
                if(null != httpResponse){
                    httpResponse.close();
                }
            } catch (IOException e) {
                log.error("关闭流失败", e);
            }
        }
        return result;
    }
}
