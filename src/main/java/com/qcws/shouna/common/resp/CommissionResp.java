package com.qcws.shouna.common.resp;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class CommissionResp {
	
	private BigDecimal commission ;
	
	private Integer population; 
	
	private List<LevelResp> levellist;
	
	private List<TodayLevelResp> todayLevellist;
 
}
