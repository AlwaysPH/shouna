package com.qcws.shouna.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HttpUtil {

    public static Map<String, Object> httpGet(String urlStr) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader in = null;
        StringBuffer buf = new StringBuffer();
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "text/json;charset=utf-8");
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String inputLine = in.readLine();
            while (inputLine != null) {
                buf.append(inputLine).append("\r\n");
                inputLine = in.readLine();
            }
            in.close();
        }finally {
            if(null != in){
                try {
                    in.close();
                }catch (Exception e){
                    log.error("调用阿里短信接口失败", e);
                }
            }
            if(null != conn){
                try {
                    conn.disconnect();
                }catch (Exception e){
                    log.error("调用阿里短信接口失败", e);
                }
            }
        }

        return JSON.parseObject(buf.toString(), HashMap.class);
    }

}
