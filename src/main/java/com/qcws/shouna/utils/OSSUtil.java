package com.qcws.shouna.utils;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;

import cn.hutool.core.date.DateUtil;
import com.qcws.shouna.config.OSSConfig;
import io.jboot.app.config.JbootConfigManager;

public class OSSUtil {

	private static OSSConfig config = null;
	
	public static String upload(File file) {
		if (config == null) {
			config = JbootConfigManager.me().get(OSSConfig.class);
		}
		String key = DateUtil.format(new Date(), "yyyyMMdd") + "/" + UUID.randomUUID();
		PutObjectRequest obj = new PutObjectRequest(config.getBucketname(), key, file);
		OSS ossClient = new OSSClientBuilder().build(config.getEndpoint(), config.getAccessKeyId(), config.getAccessKeySecret());
		ossClient.putObject(obj);
		ossClient.shutdown();
		return key;
	}

	/**
	 * 上传图片
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String uploadFile(File file) throws IOException {
		if (config == null) {
			config = JbootConfigManager.me().get(OSSConfig.class);
		}

		OSS ossClient = new OSSClientBuilder().build(config.getEndpoint(), config.getAccessKeyId(), config.getAccessKeySecret());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		// 获取文件名
		String fileName = file.getName();
		// 获取文件后缀名
		String suffixName = fileName.substring(fileName.lastIndexOf("."));
		// 最后上传生成的文件名
		String finalFileName = System.currentTimeMillis() + "" + new SecureRandom().nextInt(0x0400) + suffixName;
		// oss中的文件夹名
		String objectName = sdf.format(new Date()) + "/" + finalFileName;
		// 创建上传文件的元信息，可以通过文件元信息设置HTTP header(设置了才能通过返回的链接直接访问)。
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType("image/jpg");
		// 文件上传
		ossClient.putObject(config.getBucketname(), objectName, file,objectMetadata);
		// 设置URL过期时间为1年  3600l* 1000*24*365*1
		Date expiration = new Date(new Date().getTime() + 3600l * 1000 * 24 * 365 * 1);
		String url = ossClient.generatePresignedUrl(config.getBucketname(), objectName, expiration).toString();
		ossClient.shutdown();
		return url;
	}


}
