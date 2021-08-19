package com.qcws.shouna.model;

import io.jboot.db.annotation.Table;
import com.qcws.shouna.model.base.BaseItemCategory;

/**
 * Generated by Panda.
 */
@SuppressWarnings("serial")
@Table(tableName = "item_category", primaryKey = "id")
public class ItemCategory extends BaseItemCategory<ItemCategory> {
	public static final ItemCategory dao = new ItemCategory().dao();
}