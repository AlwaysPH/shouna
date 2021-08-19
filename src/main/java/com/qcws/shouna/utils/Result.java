package com.qcws.shouna.utils;

/**
 * 接口返回封装
 */
public class Result {

    public static final String SUCCESS = "操作成功";
    public static final String FAILURE = "操作失败";
    public static final String UNAUTHORIZED= "没有权限访问";
    public static final String PARAMERROR = "参数错误";
    public static final String USER_NOTFUND = "用户账号或密码错误";
    public static final String USERNAME_NULL = "用户名为空";
    public static final String PASSWORD_NULL = "密码为空";
    public static final String TELEPHONE_NULL = "手机号码为空";
    public static final String PID_NULL = "推荐码为空";
    public static final String TELEPHONE_EXSITS = "手机号码已存在";
    public static final String ITEMCOUNT_NULL = "商品数量不能为空";
    public static final String ORDERMONEY_NULL = "订单金额不能为空";

    private boolean result;
    private String msg;
    private Object data;

    private Result() {}

    public static Result ok() {
        return Result.ok(SUCCESS);
    }

    public static Result ok(String msg) {
        return Result.ok(msg, null);
    }

    public static Result ok(Object datas) {
        return Result.ok(SUCCESS, datas);
    }

    public static Result ok(String msg, Object data) {
        Result result = new Result();
        result.setResult(true);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static Result fail() {
        return Result.fail(FAILURE);
    }

    public static Result fail(String msg) {
        return Result.fail(msg, null);
    }

    public static Result fail(Object datas) {
        return Result.fail(FAILURE, datas);
    }

    public static Result fail(String msg, Object data) {
        Result result = new Result();
        result.setResult(false);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public boolean isResult() {
        return result;
    }
    public void setResult(boolean result) {
        this.result = result;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }

}
