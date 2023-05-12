package com.spy.usercenterbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spy.usercenterbackend.modal.entity.user.User;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 */
public interface UserService extends IService<User> {
    Long userRegister(String userAccount, String userPassword);

    boolean userLogin(String userAccount, String userPassword, HttpServletRequest request);

    Boolean userLogout(HttpServletRequest request);

    User getCurrentUser(HttpServletRequest request);
}
