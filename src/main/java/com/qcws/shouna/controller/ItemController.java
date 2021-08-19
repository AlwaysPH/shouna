package com.qcws.shouna.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import com.qcws.shouna.dto.MessageBox;
import com.qcws.shouna.model.ItemCourse;
import com.qcws.shouna.model.ItemInfo;
import com.qcws.shouna.service.ItemCourseService;
import com.qcws.shouna.service.SysConfigService;
import com.qcws.shouna.utils.UploadUtil;

import io.jboot.Jboot;
import io.jboot.db.model.Columns;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.jfinal.aop.Inject;
import com.qcws.shouna.dto.DbPageSearch;
import com.qcws.shouna.service.ItemCategoryService;
import com.qcws.shouna.service.ItemInfoService;

import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 项目管理
 * 
 * @author 唐小恕
 */
@RequiresPermissions("item_list")
@RequestMapping("/admin/item")
public class ItemController extends JbootController {

	private static final String VIDEO_TYPE = "MP4,AVI";

	@Inject ItemCategoryService itemCategoryService;
	@Inject ItemInfoService itemInfoService;
	@Inject	ItemCourseService itemCourseService;
	@Inject
	SysConfigService sysConfigService;
	
	public void index() {
		set("cs", itemInfoService.findAll());
	}
	
	public void grid() {
		DbPageSearch ps = DbPageSearch.instance(this);
		ps.select("select i.*, c.name as cname, s.content color, sc.content size");
		ps.from("from item_info i left join item_category c on i.category_id = c.id LEFT JOIN sys_config s on i.color_id = s.id LEFT JOIN sys_config sc on i.size_id = sc.id");
		ps.addExcept("i.name like ?", DbPageSearch.ALLLIKE);
		ps.addExcept("or i.code = ?");
		ps.addExcept("or i.text like ?", DbPageSearch.ALLLIKE);
		ps.addExcept("or c.name like ?", DbPageSearch.ALLLIKE);
		ps.addExcept("or c.code = ?");
		ps.orderBy("sort asc");
		renderJson(ps.toDataGrid());
	}

	public void add(){
		set("a",itemCategoryService.findAll());
		set("c", sysConfigService.findListByColumns(Columns.create("code", "color")));
		set("s", sysConfigService.findListByColumns(Columns.create("code", "size")));
	}

	public void edit(){
		int id = getParaToInt();
		set("a",itemCategoryService.findAll());
		set("i",itemInfoService.findById(id));
		set("c", sysConfigService.findListByColumns(Columns.create("code", "color")));
		set("s", sysConfigService.findListByColumns(Columns.create("code", "size")));
	}

	public void save() throws IOException {
		UploadFile file = getFile("imgurl");
		ItemInfo itemInfo = getModel(ItemInfo.class,"i");
		String fileName = UploadUtil.uploadFile(file);

		if(StringUtils.isNotEmpty(fileName)){
			itemInfo.setImgurl(fileName);
		}
		itemInfo.setAddtime(new Date());
		itemInfo.setIsRecommend("Y");
		boolean bool = itemInfo.saveOrUpdate();
		renderHtml(new MessageBox(bool).toString());
	}

	public void upload(){
		int id = getParaToInt();
		set("c",itemInfoService.findById(id));
	}

	public void uploadVideo() {
		boolean bool = false;
		//课程，处理上传的视频文件
		List<UploadFile> uploadFiles = getFiles();
		ItemCourse course = getModel(ItemCourse.class, "i");
		ItemInfo itemInfo = itemInfoService.findById(course.getItemId());
		String code = itemInfo.getCode();
		String url = Jboot.configValue("jboot.web.video") + "/" +code;
		FileUtil.mkdir(url);
		//仅支持MP4，avi格式视频
		for(UploadFile file : uploadFiles){
			String type = file.getOriginalFileName().substring(file.getOriginalFileName().length() - 3, file.getOriginalFileName().length());
			if(VIDEO_TYPE.contains(type.toUpperCase())){
				String fix = file.getOriginalFileName().substring(file.getOriginalFileName().lastIndexOf("."));
				String newFileName = System.currentTimeMillis() + RandomUtil.randomNumbers(8) + fix;
				String fileName = url + "/" + newFileName;
				File dest = new File(fileName);
				FileUtil.move(file.getFile(), dest, true);
				course.setDataUrl(Jboot.configValue("jboot.web.videoHost")+ code + "/" + newFileName);
				bool = course.saveOrUpdate();
			}
		}
		itemInfo.setVideoUrl(url);
		itemInfo.saveOrUpdate();
		renderHtml(new MessageBox(bool).toString());
	}

	public void uploadPoster(){
		set("id", getParaToInt());
	}

	public void savePoster(){
		boolean bool = false;
		UploadFile file = getFile("sharingUrl");
		String fileName = UploadUtil.uploadFile(file);
		Integer id = getParaToInt("id");
		ItemInfo info = itemInfoService.findById(id);
		if(null != info){
			info.setSharingurl(fileName);
			bool = info.saveOrUpdate();
		}
		renderHtml(new MessageBox(bool).toString());
	}

	public void uploadItemDetail(){
		set("id", getParaToInt());
	}

	public void saveItemDetail(){
		List<UploadFile> uploadFiles = getFiles();
		Integer id = getParaToInt("id");
		String isCover = getPara("isCover");
		ItemInfo itemInfo = itemInfoService.findById(id);
		StringBuilder sb = new StringBuilder();
		//不覆盖，则追加
		if("0".equals(isCover)){
			if(StringUtils.isNotEmpty(itemInfo.getText())){
				sb.append(itemInfo.getText());
			}
		}
		for(UploadFile file : uploadFiles){
			String fileName = UploadUtil.uploadFile(file);
			sb.append("<img src=\"").append(fileName).append("\">");
		}
		itemInfo.setText(sb.toString());
		renderHtml(new MessageBox(itemInfo.saveOrUpdate()).toString());
	}

	public void delete(){
		int id = getParaToInt();
		Boolean bool = itemInfoService.deleteById(id);
		renderJson(bool ? Ret.ok() : Ret.fail());
	}
	
}
