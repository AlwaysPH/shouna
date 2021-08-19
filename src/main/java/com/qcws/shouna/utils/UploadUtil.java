package com.qcws.shouna.utils;

import java.io.File;
import com.jfinal.upload.UploadFile;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import io.jboot.Jboot;

public class UploadUtil {

	public static String uploadFile(UploadFile file) {
		if (file == null || file.getFile() == null) {
			return null;
		}
		String host = Jboot.configValue("jboot.web.upload");
		String fix = file.getOriginalFileName().substring(file.getOriginalFileName().lastIndexOf("."));
		String newFileName = System.currentTimeMillis() + RandomUtil.randomNumbers(8) + fix;
		File dest = new File(host + newFileName);
		FileUtil.move(file.getFile(), dest, true);
		return Jboot.configValue("jboot.web.imghost") + newFileName;
	}
	
}
