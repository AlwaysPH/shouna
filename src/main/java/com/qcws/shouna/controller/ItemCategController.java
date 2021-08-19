package com.qcws.shouna.controller;

import com.qcws.shouna.dto.MessageBox;
import com.qcws.shouna.utils.UploadUtil;

import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import com.qcws.shouna.dto.PageSearch;
import com.qcws.shouna.dto.SimpleDatagrid;
import com.qcws.shouna.model.ItemCategory;
import com.qcws.shouna.service.ItemCategoryService;

import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

import java.io.IOException;

@RequiresPermissions("item_category")
@RequestMapping("/admin/item/categ")
public class ItemCategController extends JbootController {

	@Inject ItemCategoryService itemCategoryService;
	
	public void index() {}
	
	public void grid() {
		PageSearch ps = PageSearch.instance(this);
		Page<ItemCategory> page = itemCategoryService.paginateByColumns(
				ps.getPageNumber(),
				ps.getPageSize(),
				ps.getColumns("name", "code"),
				ps.getOrderBy());
		renderJson(new SimpleDatagrid(page));
	}
	
	public void add() {}
	
	public void edit() {
		Integer id = getParaToInt();
		set("c", itemCategoryService.findById(id));
	}
	
	public void content() {
		Integer id = getParaToInt();
		set("c", itemCategoryService.findById(id));
	}
	
	public void saveContent() {
		Integer id = getParaToInt("id");
		String content = getPara("content");
		int row = Db.update("update item_category set text = ? where id = ?", content, id);
		renderJson(row > 0 ? Ret.ok() : Ret.fail());
	}
	
	public void save() throws IOException {
		UploadFile image = getFile("image");
		ItemCategory ic = getModel(ItemCategory.class, "c");
		if (image != null && image.getFile() != null) {
			String img = UploadUtil.uploadFile(image);
			ic.setImg(img);
		}
		boolean bool = ic.saveOrUpdate();
		renderHtml(new MessageBox(bool).toString());
	}
	
	public void delete() {
		int id = getParaToInt();
		boolean success = itemCategoryService.deleteById(id);
		renderJson(success ? Ret.ok() : Ret.fail());
	}
}
