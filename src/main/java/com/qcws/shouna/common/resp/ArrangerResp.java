package com.qcws.shouna.common.resp;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class ArrangerResp {
	private Integer id;
	private String orderNo;
	private Integer arrangerId;
	private String realname;
	private String code;
	private String grade;
	private BigDecimal amount;
	private Integer status;
	private String imgurl;
	private String text;
	private Date addtime;
	private Integer frequency;
	private Integer applause;
 
}
