package com.spy.usercenterbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spy.usercenterbackend.common.ErrorCode;
import com.spy.usercenterbackend.exception.BusinessException;
import com.spy.usercenterbackend.mapper.UserMapper;
import com.spy.usercenterbackend.modal.entity.user.User;
import com.spy.usercenterbackend.service.UserService;
import com.spy.usercenterbackend.utils.BaseUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

import static com.spy.usercenterbackend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 用户注册
     * @param userAccount
     * @param userPassword
     * @return
     */
    @Override
    public Long userRegister(String userAccount, String userPassword) {
        synchronized (userAccount.intern()) {
            // 校验账号
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("userAccount", userAccount);

            long count = this.count(userQueryWrapper);
            if(count != 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已被注册");
            }

            User user = new User();

            user.setUserAccount(userAccount);
            user.setUserPassword(userPassword);
            user.setUsername(BaseUtil.randomUsername());
            boolean save = this.save(user);

            if(!save) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据库异常,注册失败");
            }

            return user.getId();
        }
    }

    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    @Override
    public boolean userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);

        userQueryWrapper.eq("userPassword", userPassword);
        User user = this.getOne(userQueryWrapper);

        if(user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }

        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return true;
    }

    /**
     * 用户退出
     * @param request
     * @return
     */
    @Override
    public Boolean userLogout(HttpServletRequest request) {
        if(request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public User getCurrentUser(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        User loginUser = this.getById(user.getId());

        if(loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return loginUser;
    }
}