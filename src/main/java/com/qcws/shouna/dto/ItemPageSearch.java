package com.qcws.shouna.dto;

import com.jfinal.core.Controller;

import io.jboot.db.model.Columns;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemPageSearch extends PageSearch {

	private Integer cid;	// 项目分类id
	
	public static ItemPageSearch instance(Controller controller) {
		ItemPageSearch ps = new ItemPageSearch();
		ps.setPageNumber(controller.getParaToInt("page"));
		ps.setPageSize(controller.getParaToInt("recPerPage"));
		ps.setSortBy(controller.getPara("sortBy"));
		ps.setOrder(controller.getPara("order"));
		ps.setSearch(controller.getPara("search"));
		ps.setCid(controller.getParaToInt("cid"));
		return ps;
	}
	
	@Override
	public Columns getColumns(String... fields) {
		Columns columns = super.getColumns(fields);
		if (cid == null || cid <= 0) {
			return columns;
		}
		return columns.eq("category_id", cid);
	}
	
}
