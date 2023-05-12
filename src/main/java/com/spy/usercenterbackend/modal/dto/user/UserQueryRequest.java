package com.spy.usercenterbackend.modal.dto.user;

import com.spy.usercenterbackend.modal.dto.common.PageRequest;

import java.util.Date;

public class UserQueryRequest extends PageRequest {
    private Long id;

    private String userAccount;

    private String username;

    private String avatarUrl;

    private String userPassword;

    private String phone;

    private String email;

    private Integer userRole;

    private Integer userStatus;

    private Date createTime;

    private Date updateTime;
}
