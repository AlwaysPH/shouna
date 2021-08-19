package com.qcws.shouna.common.resp;

import lombok.Data;

@Data
public class ItemCommentBeanResp {
	
	public ItemCommentBeanResp(String key, Long value) {
		this.title = key ;
		this.num = value;
	}
	
	private String title;
	private Long num;
 
}
