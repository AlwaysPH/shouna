package com.qcws.shouna.model;

import io.jboot.db.annotation.Table;
import com.qcws.shouna.model.base.BaseSysResource;

/**
 * Generated by Panda.
 */
@SuppressWarnings("serial")
@Table(tableName = "sys_resource", primaryKey = "id")
public class SysResource extends BaseSysResource<SysResource> {
	public static final SysResource dao = new SysResource().dao();
}
