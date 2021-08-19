package com.qcws.shouna.model.base;

import io.jboot.db.model.JbootModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by Jboot, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseCustomerRecharge<M extends BaseCustomerRecharge<M>> extends JbootModel<M> implements IBean {

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
	 * 用户id
	 */
	public void setCustomerId(java.lang.Long customerId) {
		set("customer_id", customerId);
	}

    /**
     * 用户id
     */
	public java.lang.Long getCustomerId() {
		return getLong("customer_id");
	}

	/**
	 * 支付渠道id
	 */
	public void setChannelId(java.lang.Long channelId) {
		set("channel_id", channelId);
	}

    /**
     * 支付渠道id
     */
	public java.lang.Long getChannelId() {
		return getLong("channel_id");
	}

	/**
	 * 级别id
	 */
	public void setLevelId(java.lang.Long levelId) {
		set("level_id", levelId);
	}

    /**
     * 级别id
     */
	public java.lang.Long getLevelId() {
		return getLong("level_id");
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
	 * 支付流水号
	 */
	public void setPayNo(java.lang.String payNo) {
		set("pay_no", payNo);
	}

    /**
     * 支付流水号
     */
	public java.lang.String getPayNo() {
		return getStr("pay_no");
	}

	/**
	 * 订单金额
	 */
	public void setAmount(java.math.BigDecimal amount) {
		set("amount", amount);
	}

    /**
     * 订单金额
     */
	public java.math.BigDecimal getAmount() {
		return get("amount");
	}

	/**
	 * 支付金额
	 */
	public void setPayAmount(java.math.BigDecimal payAmount) {
		set("pay_amount", payAmount);
	}

    /**
     * 支付金额
     */
	public java.math.BigDecimal getPayAmount() {
		return get("pay_amount");
	}

	/**
	 * 状态
	 */
	public void setStatus(java.lang.String status) {
		set("status", status);
	}

    /**
     * 状态
     */
	public java.lang.String getStatus() {
		return getStr("status");
	}

	/**
	 * 支付类型
	 */
	public void setPaytype(java.lang.String paytype) {
		set("paytype", paytype);
	}

    /**
     * 支付类型
     */
	public java.lang.String getPaytype() {
		return getStr("paytype");
	}

	/**
	 * 支付地址
	 */
	public void setPayurl(java.lang.String payurl) {
		set("payurl", payurl);
	}

    /**
     * 支付地址
     */
	public java.lang.String getPayurl() {
		return getStr("payurl");
	}

	/**
	 * 支付数据
	 */
	public void setPaydata(java.lang.String paydata) {
		set("paydata", paydata);
	}

    /**
     * 支付数据
     */
	public java.lang.String getPaydata() {
		return getStr("paydata");
	}

	/**
	 * 下单时间
	 */
	public void setAddtime(java.util.Date addtime) {
		set("addtime", addtime);
	}

    /**
     * 下单时间
     */
	public java.util.Date getAddtime() {
		return get("addtime");
	}

	/**
	 * 支付时间
	 */
	public void setPaytime(java.util.Date paytime) {
		set("paytime", paytime);
	}

    /**
     * 支付时间
     */
	public java.util.Date getPaytime() {
		return get("paytime");
	}

}