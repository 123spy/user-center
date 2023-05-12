package com.spy.usercenterbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spy.usercenterbackend.common.ErrorCode;
import com.spy.usercenterbackend.exception.BusinessException;
import com.spy.usercenterbackend.mapper.UserMapper;
import com.spy.usercenterbackend.modal.entity.User;
import com.spy.usercenterbackend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

import static com.spy.usercenterbackend.constant.UserConstant.SALT;
import static com.spy.usercenterbackend.constant.UserConstant.USER_LOGIN_STATE;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Override
    public Long userRegister(String userAccount, String userPassword) {
        // 校验账号
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);
        userQueryWrapper.eq("userPassword", userPassword);

        long count = this.count(userQueryWrapper);
        if(count != 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已被注册");
        }

        String md5UserPassword = DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes(StandardCharsets.UTF_8));

        User user = new User();

        user.setUserAccount(userAccount);
        user.setUserPassword(md5UserPassword);
        boolean save = this.save(user);

        if(!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据库异常,注册失败");
        }

        return user.getId();
    }

    @Override
    public boolean userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);

        String md5UserPassword = DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes(StandardCharsets.UTF_8));
        userQueryWrapper.eq("userPassword", md5UserPassword);
        User user = this.getOne(userQueryWrapper);

        if(user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }

        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return true;
    }
}