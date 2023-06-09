package com.spy.usercenterbackend.modal.dto.user;

import lombok.Data;

/**
 * 用户添加类
 */
@Data
public class UserAddRequest {
    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户身份
     */
    private String userRole;

    /**
     * 用户状态
     */
    private Integer userStatus;
}
