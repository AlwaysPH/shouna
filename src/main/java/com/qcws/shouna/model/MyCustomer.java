package com.qcws.shouna.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class MyCustomer implements Serializable {

    private static final long serialVersionUID = -1349603225038151114L;
    private Long pcount;

    private BigDecimal amount;

    private Date time;

    private String name;

    private String headImg;
}
