package com.qcws.shouna.model;

import io.jboot.db.annotation.Table;
import com.qcws.shouna.model.base.BaseSysBankInfo;

/**
 * Generated by Panda.
 */
@SuppressWarnings("serial")
@Table(tableName = "sys_bank_info", primaryKey = "id")
public class SysBankInfo extends BaseSysBankInfo<SysBankInfo> {
	public static final SysBankInfo dao = new SysBankInfo().dao();
}