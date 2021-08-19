package com.qcws.shouna.dto;

import com.jfinal.core.Controller;

import cn.hutool.core.util.StrUtil;
import io.jboot.db.model.Columns;
import lombok.Data;

@Data
public class PageSearch {
	private int pageNumber;
	private int pageSize;
	private String sortBy;
	private String order;
	private String search;
	
	public static PageSearch instance(Controller controller) {
		PageSearch ps = new PageSearch();
		ps.setPageNumber(controller.getParaToInt("page"));
		ps.setPageSize(controller.getParaToInt("recPerPage"));
		ps.setSortBy(controller.getPara("sortBy"));
		ps.setOrder(controller.getPara("order"));
		ps.setSearch(controller.getPara("search", StrUtil.EMPTY));
		return ps;
	}
	
	public Columns getColumns(String... fields) {
		Columns columns = Columns.create();
		if (StrUtil.isEmpty(this.search) || fields == null || fields.length == 0) {
			return columns;
		}
		for (String field : fields) {
			columns.likeAppendPercent(field, search.trim()).or();
		}
		return columns;
	}

	public String getOrderBy() {
		if (StrUtil.isEmpty(this.sortBy)) {
			return "id " + this.order;
		}
		return this.sortBy + StrUtil.SPACE + this.order;
	}
}
