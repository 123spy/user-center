package com.spy.usercenterbackend.exception;


import com.spy.usercenterbackend.common.ErrorCode;

/**
 * 自定义异常类
 */
public class BusinessException extends RuntimeException{
    private final Integer code;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }


    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public Integer getCode() {
        return code;
    }
}
