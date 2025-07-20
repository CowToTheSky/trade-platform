package com.platform.common.vo;

import lombok.Data;

@Data
public class ResponseVO<T> {
    private int code;         // 状态码
    private String message;   // 提示信息
    private T data;           // 业务数据
    private boolean success;  // 是否成功

    // 静态工厂方法
    public static <T> ResponseVO<T> success(T data) {
        return success(data, "操作成功");
    }

    public static <T> ResponseVO<T> success(T data, String message) {
        ResponseVO<T> vo = new ResponseVO<>();
        vo.setCode(200);
        vo.setMessage(message);
        vo.setData(data);
        vo.setSuccess(true);
        return vo;
    }

    public static <T> ResponseVO<T> error(int code, String message) {
        return error(code, message, null);
    }

    public static <T> ResponseVO<T> error(int code, String message, T data) {
        ResponseVO<T> vo = new ResponseVO<>();
        vo.setCode(code);
        vo.setMessage(message);
        vo.setData(data);
        vo.setSuccess(false);
        return vo;
    }
}
