package com.ldw.microservice.config;


import java.io.Serializable;

/**
 *
 * @author 卢丹文
 * @description: 接口返回对接
 **/
public class InternalPayload<T> implements Serializable {
    private T payload;
    private String code = "0";
    private String msg = "ok";

    public InternalPayload() {
    }

    public static <T> InternalPayload<T> of(T payload) {
        InternalPayload<T> result = new InternalPayload();
        result.payload = payload;
        return result;
    }

    public static <T> InternalPayload<T> of(String code, String msg, T payload) {
        InternalPayload<T> result = new InternalPayload();
        result.payload = payload;
        result.code = code;
        result.msg = msg;
        return result;
    }

    public T getPayload() {
        return this.payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "InternalPayload{payload=" + this.payload + ", code='" + this.code + '\'' + ", msg='" + this.msg + '\'' + '}';
    }
}
