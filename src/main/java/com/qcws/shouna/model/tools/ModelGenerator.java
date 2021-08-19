package com.qcws.shouna.model.tools;

import com.jfinal.kit.PathKit;

import io.jboot.app.JbootApplication;
import io.jboot.codegen.model.JbootBaseModelGenerator;
import io.jboot.codegen.model.JbootModelGenerator;
import io.jboot.codegen.service.JbootServiceImplGenerator;
import io.jboot.codegen.service.JbootServiceInterfaceGenerator;

public class ModelGenerator {
	
	
	public static void main(String[] args) {
		JbootApplication.setBootArg("jboot.datasource.url", "jdbc:mysql://1.15.121.198:3306/hpshop");
		JbootApplication.setBootArg("jboot.datasource.user", "root");
		JbootApplication.setBootArg("jboot.datasource.password", "Woxihuanplmm123");
        
        String modelPackage = "com.qcws.shouna.model";
        String baseModelPackage = modelPackage + ".base";
        String servicePackage = "com.qcws.shouna.service";
        String modelDir = PathKit.getWebRootPath() + "/src/main/java/" + modelPackage.replace(".", "/");
        String baseModelDir = PathKit.getWebRootPath() + "/src/main/java/" + baseModelPackage.replace(".", "/");
        
        System.out.println("start generate...");
        JbootBaseModelGenerator jbmg = new JbootBaseModelGenerator(baseModelPackage, baseModelDir);
        jbmg.generate();
        
        JbootModelGenerator jmg = new JbootModelGenerator(modelPackage, baseModelPackage, modelDir);
        jmg.setGenerateDaoInModel(true);
        jmg.setTemplate("/com/qcws/shouna/model/tools/model_template.tpl");
        jmg.generate();
        System.out.println("生成完毕... success");
        
        JbootServiceInterfaceGenerator jsf = new JbootServiceInterfaceGenerator(servicePackage, modelPackage);
        jsf.generate();
        JbootServiceImplGenerator jsig = new JbootServiceImplGenerator(servicePackage, modelPackage);
        jsig.generate();
	}
	
}
