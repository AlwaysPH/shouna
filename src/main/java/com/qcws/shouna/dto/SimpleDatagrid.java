package com.qcws.shouna.dto;

import com.jfinal.plugin.activerecord.Page;

public class SimpleDatagrid extends DataGrid {

	public SimpleDatagrid(Page<?> page) {
		super(page);
	}

	@Override
	public Object convert(Object src) {
		return src;
	}
	
}
