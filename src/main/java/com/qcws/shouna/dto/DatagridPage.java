package com.qcws.shouna.dto;

import lombok.Data;

@Data
public class DatagridPage {
	private int page;		// 当前数据对应的页码
	private int recTotal;	// 总的数据数目
	private int recPerPage;	// 每页数据数目
}
