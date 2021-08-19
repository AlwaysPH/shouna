package com.qcws.shouna.common.resp;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class OrderInfoResp{

	private Integer id;
	private Integer customerId;
	private String orderNo;
	private Integer itemId;
	private String itemName;
	private BigDecimal price ;
	private String status;
	private String settleStatus ;
	private BigDecimal commission ;
	private BigDecimal orderRate;
	private Date countdown;
	private Date addtime;
	private Date overtime;
	private BigDecimal refundAmount ;
	private String telphone;
	private String province;
	private String city ;
	private String countdowns;
	private BigDecimal doorAmount;
	private Integer arrangerId;
	private String arrangername;
	private String arrangerimgurl;
	private String arrangerphone;
	private Integer frequency;
	private Integer applause;
	private BigDecimal finalAmount ;
	private BigDecimal advanceAmount ;
	private String remark;
	private Integer commentStatus;
	public Integer userType; 
	public String reachStatus;
	public BigDecimal deposit;
}
