package com.qcws.shouna.model;

import io.jboot.db.annotation.Table;
import com.qcws.shouna.model.base.BaseCustomerOrder;

/**
 * Generated by Panda.
 */
@SuppressWarnings("serial")
@Table(tableName = "customer_order", primaryKey = "id")
public class CustomerOrder extends BaseCustomerOrder<CustomerOrder> {
	public static final CustomerOrder dao = new CustomerOrder().dao();
}