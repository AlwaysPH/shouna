package com.qcws.shouna.model.base;

import io.jboot.db.model.JbootModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by Jboot, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseItemCourse<M extends BaseItemCourse<M>> extends JbootModel<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setItemId(java.lang.Integer itemId) {
		set("item_id", itemId);
	}

	public java.lang.Integer getItemId() {
		return getInt("item_id");
	}

	public void setGroupName(java.lang.String groupName) {
		set("group_name", groupName);
	}

	public java.lang.String getGroupName() {
		return getStr("group_name");
	}

	public void setTitle(java.lang.String title) {
		set("title", title);
	}

	public java.lang.String getTitle() {
		return getStr("title");
	}

	public void setSort(java.lang.Integer sort) {
		set("sort", sort);
	}

	public java.lang.Integer getSort() {
		return getInt("sort");
	}

	public void setDataUrl(java.lang.String dataUrl) {
		set("data_url", dataUrl);
	}

	public java.lang.String getDataUrl() {
		return getStr("data_url");
	}

	public void setContent(java.lang.String content) {
		set("content", content);
	}

	public java.lang.String getContent() {
		return getStr("content");
	}

}
