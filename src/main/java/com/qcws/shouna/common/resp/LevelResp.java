package com.qcws.shouna.common.resp;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class LevelResp {
	
private Long level;
	
	private Integer id; 
	
	private String title;
	
	private Integer num;
	
	private BigDecimal orderRate;
 
}
