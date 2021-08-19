package com.qcws.shouna.model;

import io.jboot.db.annotation.Table;
import com.qcws.shouna.model.base.BaseSysMessage;

/**
 * Generated by Panda.
 */
@SuppressWarnings("serial")
@Table(tableName = "sys_message", primaryKey = "id")
public class SysMessage extends BaseSysMessage<SysMessage> {
	public static final SysMessage dao = new SysMessage().dao();
}