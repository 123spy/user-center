package com.spy.usercenterbackend.modal.dto.user;

import lombok.Data;

@Data
public class UserLoginRequest {

    private String userAccount;

    private String userPassword;
}
