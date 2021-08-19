package com.qcws.shouna.dto;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

@Data
public abstract class DataGrid implements IAdapter {
	private String result;
	private String message;
	private DatagridPage pager;
	private List<Object> data;
	
	public DataGrid(Page<?> page) {
		this.result = "success";
		this.message = StrUtil.EMPTY;
		this.data = new ArrayList<>();
		page.getList().forEach(item -> {
			data.add(this.convert(item));
		});
		this.pager =  new DatagridPage();
		this.pager.setPage(page.getPageNumber());
		this.pager.setRecPerPage(page.getPageSize());
		this.pager.setRecTotal(page.getTotalRow());
	}
	
}
