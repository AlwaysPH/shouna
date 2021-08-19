package com.qcws.shouna.common.resp;

import com.qcws.shouna.model.ItemComment;

import lombok.Data;
@Data
public class ItemCommentResp {
	
private ItemComment itemComment;
private String[] imgs;
}
