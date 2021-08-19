package com.qcws.shouna.dto;

import com.jfinal.kit.Kv;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;

import lombok.Data;

@Data
public class MessageBox {
	
	private static final String html = "<!DOCTYPE html>\r\n"
			+ "<html lang=\"zh-cn\">\r\n"
			+ "  <head>\r\n"
			+ "    <meta charset=\"utf-8\">\r\n"
			+ "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\r\n"
			+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\r\n"
			+ "    <title>提示消息</title>\r\n"
			+ "    <link rel=\"stylesheet\" href=\"/assets/zui/css/zui.min.css\">\r\n"
			+ "  <body style=\"padding:10px;\">\r\n"
			+ "    <div class=\"alert #(m.style) with-icon\">\r\n"
			+ "      <i class=\"#(m.icon)\"></i>"
			+ "      <div class=\"content\">\r\n"
			+ "        <h4>系统提示</h4>\r\n"
			+ "        <hr>\r\n"
			+ "        <p>#(m.message)</p>\r\n"
			+ "      </div>\r\n"
			+ "    </div>\r\n"
			+ "  </body>\r\n"
			+ "</html>";
	
	private String style;
	private String icon;
	private String message;
	
	public MessageBox(boolean success) {
		this.style = success ? "alert-success" : "alert-danger";
		this.icon = success ? "icon-ok-sign" : "icon-remove-sign";
		this.message = success ? "恭喜您，操作成功！" : "对不起，您刚才的操作出现了异常";
	}
	
	@Override
	public String toString() {
		Engine engine = Engine.use();
		Template template = engine.getTemplateByString(html);
		return template.renderToString(Kv.by("m", this));
	}
	
	
}
