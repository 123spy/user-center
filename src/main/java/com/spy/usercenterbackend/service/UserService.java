package com.spy.usercenterbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spy.usercenterbackend.modal.entity.User;

import javax.servlet.http.HttpServletRequest;

public interface UserService extends IService<User> {
    Long userRegister(String userAccount, String userPassword);

    boolean userLogin(String userAccount, String userPassword, HttpServletRequest request);
}
