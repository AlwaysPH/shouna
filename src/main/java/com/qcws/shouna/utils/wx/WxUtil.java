package com.qcws.shouna.utils.wx;
 
import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.hutool.http.HttpUtil;
import io.jboot.Jboot;
import lombok.extern.slf4j.Slf4j;
/**
 * 微信小程序辅助工具类 https://developers.weixin.qq.com/miniprogram/dev/api-backend/
 * @Author zhengkai.blog.csdn.net
 * */
@Slf4j
public class WxUtil {
    public static String GET_MINICODE_URL="https://api.weixin.qq.com/wxa/getwxacode";
    
    public static String getAccessTokenAsUrl(String appid,String secret){
    	 
        String tokenStr=HttpUtil.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appid+"&secret="+secret+"");
        log.info(tokenStr);
        JSONObject jsonObject = JSONObject.parseObject(tokenStr);
        return "?access_token="+jsonObject.getString("access_token");
    }
    /**
     * 下载带参数的小程序二维码
     * https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/qr-code/wxacode.getUnlimited.html
     * by zhengkai.blog.csdn.net
     * */
    public static String downloadMiniCode(String appid,String secret,Integer id,String scene){
    	 JSONObject paramJson = new JSONObject();
//         paramJson.put("scene", scene);
         paramJson.put("path", "pages/index/index"+scene);
//         paramJson.put("is_hyaline",true);
     
        
        String host = Jboot.configValue("jboot.web.upload");
        String imgFilePath = id+".png";// 新生成的图片
        try
        {
        	String urlstr=GET_MINICODE_URL+getAccessTokenAsUrl(appid,secret);
        	System.out.println(urlstr);
        	System.out.println(scene);
            URL url = new URL(urlstr);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");// 提交模式
            httpURLConnection.setConnectTimeout(10000);//连接超时 单位毫秒
            httpURLConnection.setReadTimeout(10000);//读取超时 单位毫秒
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            printWriter.write(paramJson.toString());
            // flush输出流的缓冲
            printWriter.flush();
            //开始获取数据
            BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
            OutputStream os = new FileOutputStream(new File(host+imgFilePath));
            int len;
            //设置缓冲写入
            byte[] arr = new byte[2048];
            while ((len = bis.read(arr)) != -1)
            {
                os.write(arr, 0, len);
                os.flush();
            }
            os.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return Jboot.configValue("jboot.web.imghost") + imgFilePath;
    }

/**
	 * 用于获取access_token
	 * @return  access_token
	 * @throws Exception
	 */
	public static String postToken() throws Exception {
		String APIKEY = "wx3b3dca10fc04b1af";//小程序id
		String SECRETKEY = "af622e04db0f0bfe1af53d3262ec09d7";//小程序密钥
		String requestUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+APIKEY+"&secret="+SECRETKEY;
        URL url = new URL(requestUrl);
        // 打开和URL之间的连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        // 设置通用的请求属性
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);
 
        // 得到请求的输出流对象
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes("");
        out.flush();
        out.close();
 
        // 建立实际的连接
        connection.connect();
        // 定义 BufferedReader输入流来读取URL的响应
        BufferedReader in = null;
        if (requestUrl.contains("nlp"))
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "GBK"));
        else
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        String result = "";
        String getLine;
        while ((getLine = in.readLine()) != null) {
            result += getLine;
        }
        in.close();
        JSONObject jsonObject = JSON.parseObject(result);
		String accesstoken=jsonObject.getString("access_token");
        return accesstoken;
    }

/**
	 * 生成带参小程序二维码
	 * @param sceneStr	参数
	 * @param accessToken	token
	 */
	 public static void getminiqrQr(String sceneStr, String accessToken) {
		 try
	        {
	            URL url = new URL("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token="+accessToken);
	            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
	            httpURLConnection.setRequestMethod("POST");// 提交模式
	            // conn.setConnectTimeout(10000);//连接超时 单位毫秒
	            // conn.setReadTimeout(2000);//读取超时 单位毫秒
	            // 发送POST请求必须设置如下两行
	            httpURLConnection.setDoOutput(true);
	            httpURLConnection.setDoInput(true);
	            // 获取URLConnection对象对应的输出流
	            PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
	            // 发送请求参数
	            JSONObject paramJson = new JSONObject();
	            paramJson.put("scene", sceneStr);
	            paramJson.put("page", "pages/index/index");
	            paramJson.put("width", 430);
	            paramJson.put("auto_color", true);
	            /**
	             * line_color生效
	             * paramJson.put("auto_color", false);
	             * JSONObject lineColor = new JSONObject();
	             * lineColor.put("r", 0);
	             * lineColor.put("g", 0);
	             * lineColor.put("b", 0);
	             * paramJson.put("line_color", lineColor);
	             * */
 
	            printWriter.write(paramJson.toString());
	            // flush输出流的缓冲
	            printWriter.flush();
	            //开始获取数据
	            BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
	            OutputStream os = new FileOutputStream(new File("C:/Users/Administrator/Desktop/1.png"));
	            int len;
	            byte[] arr = new byte[1024];
	            while ((len = bis.read(arr)) != -1)
	            {
	                os.write(arr, 0, len);
	                os.flush();
	            }
	            os.close();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }

    public static KeyStore getCertificate(String mchId) {
        //try-with-resources 关流
        StringBuilder sb = new StringBuilder();
        sb.append(File.separator).append("data").append(File.separator).append("shouna")
                .append(File.separator).append("secret").append(File.separator).append("apiclient_cert.p12");
        try (FileInputStream inputStream = new FileInputStream(new File(sb.toString()))) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(inputStream, mchId.toCharArray());
            return keyStore;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
	 
//	 public static String getCourseMap() throws Exception{
//		 String assessToken = WxUtil.postToken();
//		 String weixin_url ="https://api.weixin.qq.com/cgi-bin/wxaapp/createwxaqrcode?access_token="+assessToken;
//		 JSONObject js = new JSONObject();
//		 js.put("access_token", assessToken);
//		 js.put("path", "pages/index/index?id=6&sid=7");
//		 String result = WxUtil.httpsRequestToMap(weixin_url, "POST", js.toJSONString());
//		
//		 return result;
//		 }
	 public static void main(String[] args) throws Exception {
	
		WxUtil.downloadMiniCode("wx3b3dca10fc04b1af", "af622e04db0f0bfe1af53d3262ec09d7", 1, "?id=6&sid=7");
//		System.out.println(accessToken);;
	}


}

