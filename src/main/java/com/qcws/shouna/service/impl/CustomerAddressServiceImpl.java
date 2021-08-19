package com.qcws.shouna.service.impl;

import io.jboot.aop.annotation.Bean;
import com.qcws.shouna.service.CustomerAddressService;
import com.qcws.shouna.model.CustomerAddress;
import io.jboot.service.JbootServiceBase;


@Bean
public class CustomerAddressServiceImpl extends JbootServiceBase<CustomerAddress> implements CustomerAddressService {

}