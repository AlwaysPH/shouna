package com.qcws.shouna.service.impl;

import io.jboot.aop.annotation.Bean;
import com.qcws.shouna.service.CustomerInfoService;
import com.qcws.shouna.model.CustomerInfo;
import io.jboot.service.JbootServiceBase;


@Bean
public class CustomerInfoServiceImpl extends JbootServiceBase<CustomerInfo> implements CustomerInfoService {

}