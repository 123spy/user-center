package com.spy.usercenterbackend.modal.dto.user;

import lombok.Data;

import java.util.Date;

/**
 * 用户更新类
 */
@Data
public class UserUpdateRequest {
    /**
     * 用户ID
     */
    private Long id;

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
     * 用户密码
     */
    private String userPassword;

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 用户邮箱
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
