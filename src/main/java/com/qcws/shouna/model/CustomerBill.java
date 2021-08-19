package com.qcws.shouna.model;

import io.jboot.db.annotation.Table;
import com.qcws.shouna.model.base.BaseCustomerBill;

/**
 * Generated by Panda.
 */
@SuppressWarnings("serial")
@Table(tableName = "customer_bill", primaryKey = "id")
public class CustomerBill extends BaseCustomerBill<CustomerBill> {
	public static final CustomerBill dao = new CustomerBill().dao();
}
