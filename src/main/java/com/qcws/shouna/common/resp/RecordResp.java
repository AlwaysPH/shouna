package com.qcws.shouna.common.resp;

import java.util.List;

import com.jfinal.plugin.activerecord.Record;

import lombok.Data;

@Data
public class RecordResp {  
	private Record record;
	private List<Record> items;
}
