package com.qcws.shouna.model;

public enum CustomerEnum {

    COMMON_USER("COMMON_USER", "普通用户"),

    PARTNER("PARTNER","合伙人"),

    FIRST_FRANCHISEE("FIRST_FRANCHISEE","加盟商一级"),

    SECOND_FRANCHISEE("SECOND_FRANCHISEE","加盟商二级"),

    FRANCHISEE("FRANCHISEE","加盟商"),

    CITY_PARTNER("CITY_PARTNER","城市合伙人");

    private String name;

    private String value;

    CustomerEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
