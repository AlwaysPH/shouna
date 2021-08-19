package com.qcws.shouna.model;

import io.jboot.db.annotation.Table;
import com.qcws.shouna.model.base.BaseSysCity;

/**
 * Generated by Panda.
 */
@SuppressWarnings("serial")
@Table(tableName = "sys_city", primaryKey = "id")
public class SysCity extends BaseSysCity<SysCity> {
	public static final SysCity dao = new SysCity().dao();
}
