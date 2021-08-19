package com.qcws.shouna.common.resp;

import java.util.List;

import lombok.Data;


@Data
public class OrderarrangerBeanResp {
	private OrderResp orderResp;
	private List<ArrangerResp> arrangerRespList;
 
}
