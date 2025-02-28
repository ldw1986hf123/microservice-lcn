package com.ldw.mircoservice.vo;

public class Result<T> {
    private int code;         // 状态码，例如 200 表示成功，400 表示失败
    private String message;   // 描述信息
    private T data;           // 泛型数据，具体业务返回内容

    // 构造函数
    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // Getter 和 Setter
    public int getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }

    // 静态方法，便于快速构造成功或失败结果
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "Success", data);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }
}
