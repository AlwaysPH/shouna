package com.qcws.shouna.common.resp;

import java.util.List;

import com.qcws.shouna.model.ItemCourse;

public class ItemCourseBeanResp {
	private String title;
	private List<ItemCourse> itemCourseList;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<ItemCourse> getItemCourseList() {
		return itemCourseList;
	}

	public void setItemCourseList(List<ItemCourse> itemCourseList) {
		this.itemCourseList = itemCourseList;
	}

}
