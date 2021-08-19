package com.qcws.shouna.dto;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DbPageSearch extends PageSearch {
	
	public static final int DEFAULT_VAL = 0;
	public static final int LEFTLIKE = 1;
	public static final int ALLLIKE = 2;
	public static final int EXIST_VAL = 3;

	private String select;
	private StringBuffer excepts;
	private List<Object> params = new ArrayList<>();
	private boolean notAddExceptd = true;
	
	public static DbPageSearch instance(Controller controller) {
		DbPageSearch ps = new DbPageSearch();
		ps.setPageNumber(controller.getParaToInt("page"));
		ps.setPageSize(controller.getParaToInt("recPerPage"));
		ps.setSortBy(controller.getPara("sortBy"));
		ps.setOrder(controller.getPara("order"));
		ps.setSearch(controller.getPara("search", StrUtil.EMPTY));
		return ps;
	}
	
	public void select(String select) {
		this.setSelect(select);
	}
	
	public void from(String except) {
		this.excepts = new StringBuffer(except);
	}
	
	public void addExcept(String... excepts) {
		for (String except : excepts) {
			addExcept(except, DEFAULT_VAL);
		}
	}
	
	public void addExcept(String except, int valType) {
		if (StrUtil.isEmpty(getSearch())) {
			return;
		}
		if (this.notAddExceptd) {
			this.notAddExceptd = false;
			this.excepts.append(" where ");
		}
		if (StrUtil.isBlank(except)) {
			return;
		}
		this.excepts.append(StrUtil.SPACE).append(except.trim());
		switch (valType) {
			case LEFTLIKE:
				params.add(getSearch() + "%");
				break;
			case ALLLIKE:
				params.add("%" + getSearch() + "%");
				break;
			case EXIST_VAL:
				break;
			default:
				params.add(getSearch());
		}
		
	}

	public void addExcept(String except, int valType, String str) {
		if (this.notAddExceptd) {
			this.notAddExceptd = false;
			this.excepts.append(" where ");
		}
		if (StrUtil.isBlank(except)) {
			return;
		}
		this.excepts.append(StrUtil.SPACE).append(except.trim());
		switch (valType) {
			case LEFTLIKE:
				params.add(getSearch() + "%");
				break;
			case ALLLIKE:
				params.add("%" + getSearch() + "%");
				break;
			default:
				params.add(str);
		}
	}
	
	public void orderBy(String text) {
		this.excepts.append(" order by ").append(text);
	}

	public void groupBy(String text){
        this.excepts.append(" GROUP BY ").append(text);
    }
	
	public DataGrid toDataGrid() {
		if (this.params == null) {
			this.params = new ArrayList<Object>();
		}
		Page<Record> page = Db.paginate(getPageNumber(), getPageSize(),
				this.select, this.excepts.toString(),
				this.params.toArray());
		return new SimpleDatagrid(page);
	}


	public void addCommon(String str) {
		this.excepts.append(StrUtil.SPACE).append(str.trim());
	}
}
