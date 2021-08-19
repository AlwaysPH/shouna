package com.qcws.shouna.common.resp;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CustomerInfoResp{

	private Integer id;
	private String username;
	private String nickname;
	private BigDecimal balance;
	private BigDecimal totalCashout;
	 
}
