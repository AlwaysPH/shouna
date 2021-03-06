package com.qcws.shouna.model.base;

import io.jboot.db.model.JbootModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by Jboot, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseItemArranger<M extends BaseItemArranger<M>> extends JbootModel<M> implements IBean {

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
	 * 订单号
	 */
	public void setOrderNo(java.lang.String orderNo) {
		set("order_no", orderNo);
	}

    /**
     * 订单号
     */
	public java.lang.String getOrderNo() {
		return getStr("order_no");
	}

	/**
	 * 客户id
	 */
	public void setArrangerId(java.lang.Integer arrangerId) {
		set("arranger_id", arrangerId);
	}

    /**
     * 客户id
     */
	public java.lang.Integer getArrangerId() {
		return getInt("arranger_id");
	}

	/**
	 * 姓名
	 */
	public void setRealname(java.lang.String realname) {
		set("realname", realname);
	}

    /**
     * 姓名
     */
	public java.lang.String getRealname() {
		return getStr("realname");
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
	 * 级别 初 中 高
	 */
	public void setGrade(java.lang.String grade) {
		set("grade", grade);
	}

    /**
     * 级别 初 中 高
     */
	public java.lang.String getGrade() {
		return getStr("grade");
	}

	/**
	 * 价格
	 */
	public void setAmount(java.math.BigDecimal amount) {
		set("amount", amount);
	}

    /**
     * 价格
     */
	public java.math.BigDecimal getAmount() {
		return get("amount");
	}

	/**
	 * 0空闲 1服务中
	 */
	public void setStatus(java.lang.Integer status) {
		set("status", status);
	}

    /**
     * 0空闲 1服务中
     */
	public java.lang.Integer getStatus() {
		return getInt("status");
	}

	/**
	 * 头像
	 */
	public void setImgurl(java.lang.String imgurl) {
		set("imgurl", imgurl);
	}

    /**
     * 头像
     */
	public java.lang.String getImgurl() {
		return getStr("imgurl");
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
	 * 添加时间
	 */
	public void setAddtime(java.util.Date addtime) {
		set("addtime", addtime);
	}

    /**
     * 添加时间
     */
	public java.util.Date getAddtime() {
		return get("addtime");
	}

}
