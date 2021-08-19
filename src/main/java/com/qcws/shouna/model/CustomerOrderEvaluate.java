package com.qcws.shouna.model;

import io.jboot.db.annotation.Table;
import com.qcws.shouna.model.base.BaseCustomerOrderEvaluate;

/**
 * Generated by Panda.
 */
@SuppressWarnings("serial")
@Table(tableName = "customer_order_evaluate", primaryKey = "id")
public class CustomerOrderEvaluate extends BaseCustomerOrderEvaluate<CustomerOrderEvaluate> {
	public static final CustomerOrderEvaluate dao = new CustomerOrderEvaluate().dao();
}