package com.qcws.shouna.service.impl;

import io.jboot.aop.annotation.Bean;
import com.qcws.shouna.service.CustomerOrderService;
import com.qcws.shouna.model.CustomerOrder;
import io.jboot.service.JbootServiceBase;


@Bean
public class CustomerOrderServiceImpl extends JbootServiceBase<CustomerOrder> implements CustomerOrderService {

}