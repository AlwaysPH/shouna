package com.qcws.shouna.model;

import io.jboot.db.annotation.Table;
import com.qcws.shouna.model.base.BaseSysConfig;

/**
 * Generated by Panda.
 */
@SuppressWarnings("serial")
@Table(tableName = "sys_config", primaryKey = "id")
public class SysConfig extends BaseSysConfig<SysConfig> {
	public static final SysConfig dao = new SysConfig().dao();
}