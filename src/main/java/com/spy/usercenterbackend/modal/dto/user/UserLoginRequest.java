package com.spy.usercenterbackend.modal.dto.user;

import lombok.Data;

/**
 * 用户注册类
 */
@Data
public class UserLoginRequest {

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;
}
