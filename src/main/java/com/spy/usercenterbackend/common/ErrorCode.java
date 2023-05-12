package com.spy.usercenterbackend.common;

/**
 * 错误码
 */
public enum ErrorCode {
    SUCCESS(0, "ok"),
    NOT_LOGIN_ERROR(401, "未登录"),
    PARAMS_ERROR(402, "请求参数错误"),
    NO_AUTH_ERROR(403, "无权限"),
    NOT_FOUND_ERROR(404, "请求数据不存在"),
    FORBIDDEN_ERROR(405, "禁止访问"),
    OPERATION_ERROR(407, "操作失败"),
    SYSTEM_ERROR(500, "服务器错误");

    private final Integer code;

    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
