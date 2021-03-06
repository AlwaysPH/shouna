package com.qcws.shouna.model.base;

import io.jboot.db.model.JbootModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by Jboot, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseCustomerCommission<M extends BaseCustomerCommission<M>> extends JbootModel<M> implements IBean {

	/**
	 * ID
	 */
	public void setId(java.lang.Long id) {
		set("id", id);
	}

    /**
     * ID
     */
	public java.lang.Long getId() {
		return getLong("id");
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
	 * 来源用户ID
	 */
	public void setSourceUid(java.lang.Long sourceUid) {
		set("source_uid", sourceUid);
	}

    /**
     * 来源用户ID
     */
	public java.lang.Long getSourceUid() {
		return getLong("source_uid");
	}

	/**
	 * 等级
	 */
	public void setLevel(java.lang.Long level) {
		set("level", level);
	}

    /**
     * 等级
     */
	public java.lang.Long getLevel() {
		return getLong("level");
	}

	/**
	 * 金额
	 */
	public void setAmount(java.math.BigDecimal amount) {
		set("amount", amount);
	}

    /**
     * 金额
     */
	public java.math.BigDecimal getAmount() {
		return get("amount");
	}

	/**
	 * 类型
	 */
	public void setCommissionType(java.lang.String commissionType) {
		set("commission_type", commissionType);
	}

    /**
     * 类型
     */
	public java.lang.String getCommissionType() {
		return getStr("commission_type");
	}

	/**
	 * 下单时间
	 */
	public void setCommissionTime(java.util.Date commissionTime) {
		set("commission_time", commissionTime);
	}

    /**
     * 下单时间
     */
	public java.util.Date getCommissionTime() {
		return get("commission_time");
	}

}
