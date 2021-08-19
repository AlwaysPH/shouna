package com.qcws.shouna.common.resp;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class OrderResp{

	private Integer id;
	 
	public Integer customerId;
	 
	public String orderNo;
	
	public Integer itemId;
	
	public String itemName;

	public BigDecimal price ;
	 
	public String status;
 
	public String settleStatus ;
 
	public BigDecimal commission ;
	 
	public BigDecimal orderRate;
	 
	public Date countdown;
	 
	public Date addtime;
	 
	public Date overtime;
	 
	public BigDecimal refundAmount ;
	 
	public String telphone;
	 
	public String province;
 
	public String city ;
	  
	public String countdowns;
	
	public Integer offerStatus;  
	
	public Integer userType; 
	
	public String reachStatus;
	
	public BigDecimal deposit;
	 
}
