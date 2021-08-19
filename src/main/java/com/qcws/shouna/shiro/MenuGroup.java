package com.qcws.shouna.shiro;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class MenuGroup {
	private Integer id;
	private String title;
	private String icon;
	private List<MenuItem> items;
	
	public void addItem(MenuItem item) {
		if (items == null) {
			items = new ArrayList<MenuItem>();
		}
		items.add(item);
	}
}
