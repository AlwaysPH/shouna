package com.qcws.shouna.model.base;

import io.jboot.db.model.JbootModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by Jboot, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseCustomerInfo<M extends BaseCustomerInfo<M>> extends JbootModel<M> implements IBean {

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
	 * 用户名
	 */
	public void setUsername(java.lang.String username) {
		set("username", username);
	}

    /**
     * 用户名
     */
	public java.lang.String getUsername() {
		return getStr("username");
	}

	/**
	 * 密码
	 */
	public void setPassword(java.lang.String password) {
		set("password", password);
	}

    /**
     * 密码
     */
	public java.lang.String getPassword() {
		return getStr("password");
	}

	/**
	 * 昵称
	 */
	public void setNickname(java.lang.String nickname) {
		set("nickname", nickname);
	}

    /**
     * 昵称
     */
	public java.lang.String getNickname() {
		return getStr("nickname");
	}

	/**
	 * 微信openid
	 */
	public void setOpenid(java.lang.String openid) {
		set("openid", openid);
	}

    /**
     * 微信openid
     */
	public java.lang.String getOpenid() {
		return getStr("openid");
	}

	/**
	 * 0客户 1整理师 2 客服
	 */
	public void setType(java.lang.Integer type) {
		set("type", type);
	}

    /**
     * 0客户 1整理师
     */
	public java.lang.Integer getType() {
		return getInt("type");
	}

	/**
	 * 整理师id
	 */
	public void setArrangerId(java.lang.Integer arrangerId) {
		set("arranger_id", arrangerId);
	}

    /**
     * 整理师id
     */
	public java.lang.Integer getArrangerId() {
		return getInt("arranger_id");
	}

	/**
	 * 头像
	 */
	public void setHeadimg(java.lang.String headimg) {
		set("headimg", headimg);
	}

    /**
     * 头像
     */
	public java.lang.String getHeadimg() {
		return getStr("headimg");
	}

	/**
	 * 手机号码
	 */
	public void setTelphone(java.lang.String telphone) {
		set("telphone", telphone);
	}

    /**
     * 手机号码
     */
	public java.lang.String getTelphone() {
		return getStr("telphone");
	}

	/**
	 * 邀请码
	 */
	public void setInviteCode(java.lang.String inviteCode) {
		set("invite_code", inviteCode);
	}

    /**
     * 邀请码
     */
	public java.lang.String getInviteCode() {
		return getStr("invite_code");
	}

	/**
	 * 上级id
	 */
	public void setPid(java.lang.Integer pid) {
		set("pid", pid);
	}

    /**
     * 上级id
     */
	public java.lang.Integer getPid() {
		return getInt("pid");
	}

	/**
	 * 状态0、正常 1、禁用
	 */
	public void setStatus(java.lang.Integer status) {
		set("status", status);
	}

    /**
     * 状态0、正常 1、禁用
     */
	public java.lang.Integer getStatus() {
		return getInt("status");
	}

	/**
	 * 等级id
	 */
	public void setLevelId(java.lang.Integer levelId) {
		set("level_id", levelId);
	}

    /**
     * 等级id
     */
	public java.lang.Integer getLevelId() {
		return getInt("level_id");
	}

	/**
	 * 等级数
	 */
	public void setLevelNum(java.lang.Integer levelNum) {
		set("level_num", levelNum);
	}

    /**
     * 等级数
     */
	public java.lang.Integer getLevelNum() {
		return getInt("level_num");
	}

	/**
	 * 第几级代理
	 */
	public void setAgentLevel(java.lang.Integer agentLevel) {
		set("agent_level", agentLevel);
	}

    /**
     * 第几级代理
     */
	public java.lang.Integer getAgentLevel() {
		return getInt("agent_level");
	}

	/**
	 * 总下单数
	 */
	public void setTotalOrder(java.lang.Integer totalOrder) {
		set("total_order", totalOrder);
	}

    /**
     * 总下单数
     */
	public java.lang.Integer getTotalOrder() {
		return getInt("total_order");
	}

	/**
	 * 总下单金额
	 */
	public void setTotalMoney(java.math.BigDecimal totalMoney) {
		set("total_money", totalMoney);
	}

    /**
     * 总下单金额
     */
	public java.math.BigDecimal getTotalMoney() {
		return get("total_money");
	}

	/**
	 * 余额
	 */
	public void setBalance(java.math.BigDecimal balance) {
		set("balance", balance);
	}

    /**
     * 余额
     */
	public java.math.BigDecimal getBalance() {
		return get("balance");
	}

	/**
	 * 版本号
	 */
	public void setVersion(java.lang.Integer version) {
		set("version", version);
	}

    /**
     * 版本号
     */
	public java.lang.Integer getVersion() {
		return getInt("version");
	}

	/**
	 * 注册时间
	 */
	public void setRegtime(java.util.Date regtime) {
		set("regtime", regtime);
	}

    /**
     * 注册时间
     */
	public java.util.Date getRegtime() {
		return get("regtime");
	}

	/**
	 * 注册ip
	 */
	public void setRegip(java.lang.String regip) {
		set("regip", regip);
	}

    /**
     * 注册ip
     */
	public java.lang.String getRegip() {
		return getStr("regip");
	}

	/**
	 * 登陆时间
	 */
	public void setLogintime(java.util.Date logintime) {
		set("logintime", logintime);
	}

    /**
     * 登陆时间
     */
	public java.util.Date getLogintime() {
		return get("logintime");
	}

	/**
	 * 登录ip
	 */
	public void setLoginip(java.lang.String loginip) {
		set("loginip", loginip);
	}

    /**
     * 登录ip
     */
	public java.lang.String getLoginip() {
		return getStr("loginip");
	}

	/**
	 * 登陆时间
	 */
	public void setAgenttime(java.util.Date agenttime) {
		set("agenttime", agenttime);
	}

    /**
     * 登陆时间
     */
	public java.util.Date getAgenttime() {
		return get("agenttime");
	}

	/**
	 * 性别
	 */
	public void setSex(java.lang.String sex) {
		set("sex", sex);
	}

    /**
     * 性别
     */
	public java.lang.String getSex() {
		return getStr("sex");
	}

	/**
	 * 省份
	 */
	public void setProvince(java.lang.String province) {
		set("province", province);
	}

    /**
     * 省份
     */
	public java.lang.String getProvince() {
		return getStr("province");
	}

	/**
	 * 城市
	 */
	public void setCity(java.lang.String city) {
		set("city", city);
	}

    /**
     * 城市
     */
	public java.lang.String getCity() {
		return getStr("city");
	}

	/**
	 * 是否是城市合伙人
	 */
	public void setIsCityPartner(java.lang.String isCityPartner) {
		set("is_city_partner", isCityPartner);
	}

	/**
	 * 是否是城市合伙人
	 */
	public java.lang.String getIsCityPartner() {
		return getStr("is_city_partner");
	}

	/**
	 * 是否是客服
	 */
	public void setIsService(java.lang.String isService) {
		set("is_service", isService);
	}

	/**
	 * 是否是客服
	 */
	public java.lang.String getIsService() {
		return getStr("is_service");
	}

}
