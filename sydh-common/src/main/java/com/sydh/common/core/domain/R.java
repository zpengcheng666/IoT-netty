package com.sydh.common.core.domain;

import java.io.Serializable;


public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int SUCCESS = 200;
    public static final int FAIL = 500;
    private int code;
    private String msg;
    private T data;

    public static <T> R<T> ok() {
        return restResult(null, 200, "操作成功");
    }


    public static <T> R<T> ok(T data) {
        return restResult(data, 200, "操作成功");
    }


    public static <T> R<T> ok(T data, String msg) {
        return restResult(data, 200, msg);
    }


    public static <T> R<T> fail() {
        return restResult(null, 500, "操作失败");
    }


    public static <T> R<T> fail(String msg) {
        return restResult(null, 500, msg);
    }


    public static <T> R<T> fail(T data) {
        return restResult(data, 500, "操作失败");
    }


    public static <T> R<T> fail(T data, String msg) {
        return restResult(data, 500, msg);
    }


    public static <T> R<T> fail(int code, String msg) {
        return restResult(null, code, msg);
    }


    private static <T> R<T> restResult(T data, int code, String msg) {
        R<T> r = new R();
        r.setCode(code);
        r.setData(data);
        r.setMsg(msg);
        return r;
    }


    public int getCode() {
        return this.code;
    }


    public void setCode(int code) {
        this.code = code;
    }


    public String getMsg() {
        return this.msg;
    }


    public void setMsg(String msg) {
        this.msg = msg;
    }


    public T getData() {
        return this.data;
    }


    public void setData(T data) {
        this.data = data;
    }


    public static <T> Boolean isError(R<T> ret) {
        return Boolean.valueOf(!isSuccess(ret).booleanValue());
    }


    public static <T> Boolean isSuccess(R<T> ret) {
        return Boolean.valueOf((200 == ret.getCode()));
    }
}
