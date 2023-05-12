package com.spy.usercenterbackend.common;

import lombok.Data;

/**
 * 通用返回类
 * @param <T>
 */
@Data
public class BaseResponse<T> {

    private Integer code;

    private T data;

    private String message;

    public BaseResponse(Integer code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(Integer code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
