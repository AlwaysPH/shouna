package com.qcws.shouna.model.base;

import io.jboot.db.model.JbootModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by Jboot, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseCustomerCashout<M extends BaseCustomerCashout<M>> extends JbootModel<M> implements IBean {

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
	 * 客户id
	 */
	public void setCustomerId(java.lang.Integer customerId) {
		set("customer_id", customerId);
	}

    /**
     * 客户id
     */
	public java.lang.Integer getCustomerId() {
		return getInt("customer_id");
	}

	/**
	 * 开户姓名
	 */
	public void setAccountName(java.lang.String accountName) {
		set("account_name", accountName);
	}

    /**
     * 开户姓名
     */
	public java.lang.String getAccountName() {
		return getStr("account_name");
	}

	/**
	 * 银行名
	 */
	public void setBankName(java.lang.String bankName) {
		set("bank_name", bankName);
	}

    /**
     * 银行名
     */
	public java.lang.String getBankName() {
		return getStr("bank_name");
	}

	/**
	 * 卡号
	 */
	public void setCardNo(java.lang.String cardNo) {
		set("card_no", cardNo);
	}

    /**
     * 卡号
     */
	public java.lang.String getCardNo() {
		return getStr("card_no");
	}

	/**
	 * 银行地址
	 */
	public void setBankAddress(java.lang.String bankAddress) {
		set("bank_address", bankAddress);
	}

    /**
     * 银行地址
     */
	public java.lang.String getBankAddress() {
		return getStr("bank_address");
	}

	/**
	 * 提现金额
	 */
	public void setAmount(java.math.BigDecimal amount) {
		set("amount", amount);
	}

    /**
     * 提现金额
     */
	public java.math.BigDecimal getAmount() {
		return get("amount");
	}

	/**
	 * 状态0提现中1完成
	 */
	public void setStatus(java.lang.String status) {
		set("status", status);
	}

    /**
     * 状态0提现中1完成
     */
	public java.lang.String getStatus() {
		return getStr("status");
	}

	/**
	 * 备注
	 */
	public void setRemark(java.lang.String remark) {
		set("remark", remark);
	}

    /**
     * 备注
     */
	public java.lang.String getRemark() {
		return getStr("remark");
	}

	/**
	 * 处理人id
	 */
	public void setUserId(java.lang.Integer userId) {
		set("user_id", userId);
	}

    /**
     * 处理人id
     */
	public java.lang.Integer getUserId() {
		return getInt("user_id");
	}

	/**
	 * 处理人账号
	 */
	public void setUsername(java.lang.String username) {
		set("username", username);
	}

    /**
     * 处理人账号
     */
	public java.lang.String getUsername() {
		return getStr("username");
	}

	/**
	 * 申请时间
	 */
	public void setAddtime(java.util.Date addtime) {
		set("addtime", addtime);
	}

    /**
     * 申请时间
     */
	public java.util.Date getAddtime() {
		return get("addtime");
	}

	/**
	 * 结束时间
	 */
	public void setOvertime(java.util.Date overtime) {
		set("overtime", overtime);
	}

    /**
     * 结束时间
     */
	public java.util.Date getOvertime() {
		return get("overtime");
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
	 * 交易编号
	 */
	public void setPayNo(java.lang.String payNo) {
		set("pay_no", payNo);
	}

    /**
     * 交易编号
     */
	public java.lang.String getPayNo() {
		return getStr("pay_no");
	}

}