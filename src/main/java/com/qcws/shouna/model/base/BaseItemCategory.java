package com.qcws.shouna.model.base;

import io.jboot.db.model.JbootModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by Jboot, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseItemCategory<M extends BaseItemCategory<M>> extends JbootModel<M> implements IBean {

	/**
	 * 主键id
	 */
	public void setId(java.lang.Integer id) {
		set("id", id);
	}

    /**
     * 主键id
     */
	public java.lang.Integer getId() {
		return getInt("id");
	}

	/**
	 * 名称
	 */
	public void setName(java.lang.String name) {
		set("name", name);
	}

    /**
     * 名称
     */
	public java.lang.String getName() {
		return getStr("name");
	}

	/**
	 * 代码
	 */
	public void setCode(java.lang.String code) {
		set("code", code);
	}

    /**
     * 代码
     */
	public java.lang.String getCode() {
		return getStr("code");
	}

	/**
	 * 说明
	 */
	public void setText(java.lang.String text) {
		set("text", text);
	}

    /**
     * 说明
     */
	public java.lang.String getText() {
		return getStr("text");
	}

	/**
	 * 图片地址
	 */
	public void setImg(java.lang.String img) {
		set("img", img);
	}

    /**
     * 图片地址
     */
	public java.lang.String getImg() {
		return getStr("img");
	}

	/**
	 * 排序
	 */
	public void setSort(java.lang.Long sort) {
		set("sort", sort);
	}

    /**
     * 排序
     */
	public java.lang.Long getSort() {
		return getLong("sort");
	}

}