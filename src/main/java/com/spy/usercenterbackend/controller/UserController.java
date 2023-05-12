package com.spy.usercenterbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.spy.usercenterbackend.common.BaseResponse;
import com.spy.usercenterbackend.common.ErrorCode;
import com.spy.usercenterbackend.common.ResultUtils;
import com.spy.usercenterbackend.exception.BusinessException;
import com.spy.usercenterbackend.modal.dto.user.*;
import com.spy.usercenterbackend.modal.entity.User;
import com.spy.usercenterbackend.modal.vo.UserVO;
import com.spy.usercenterbackend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.spy.usercenterbackend.constant.UserConstant.SALT;
import static com.spy.usercenterbackend.constant.UserConstant.USER_LOGIN_STATE;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    // region 登陆相关

    @PostMapping("/register")
    public BaseResponse userRegister(@RequestBody UserRegisterRequest userRegisterRequest, HttpServletRequest request) {
        // 校验
        if(userRegisterRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String userPassword = userRegisterRequest.getUserPassword();
        String userAccount = userRegisterRequest.getUserAccount();

        if(StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度少于4");
        }

        if(userPassword.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度小于4");
        }

        String validPattern = "[\\u00A0\\s\"`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户含有特殊字符");
        }

        Long id = userService.userRegister(userAccount, userPassword);
        return ResultUtils.success(id);
    }

    @PostMapping("/login")
    public BaseResponse userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if(userLoginRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if(StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(userAccount.length() < 4 || userPassword.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String validPattern = "[\\u00A0\\s\"`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名不存在");
        }

        boolean login = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(login);
    }

    @GetMapping("/logout")
    public BaseResponse userLogout(HttpServletRequest request) {
        if(request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return ResultUtils.success(true);
    }

    @GetMapping("/current")
    public BaseResponse getCurrentUser(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        user = userService.getById(user.getId());
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }
    // endregion

    // region 增删改查
    @GetMapping("/get")
    public BaseResponse getUser(Long id) {
        if(id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        if(user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户未注册");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    @GetMapping("/delete")
    public BaseResponse deleteUser(Long id) {
        if(id < 0 ) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        if(user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户未注册");
        }

        boolean remove = userService.removeById(id);
        if(!remove) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据库异常，删除失败");
        }
        return ResultUtils.success(remove);
    }

    @PostMapping("/add")
    public BaseResponse addUser(@RequestBody UserAddRequest userAddRequest) {
        if(userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);

        String md5UserPassword = DigestUtils.md5DigestAsHex((userAddRequest.getUserPassword() + SALT).getBytes(StandardCharsets.UTF_8));
        user.setUserPassword(md5UserPassword);

        boolean save = userService.save(user);
        if(!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据库异常,添加失败");
        }
        return ResultUtils.success(save);
    }

    @PostMapping("/update")
    public BaseResponse updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        return ResultUtils.success(result);
    }

    @GetMapping("/list")
    public BaseResponse listUser(@RequestBody UserQueryRequest userQueryRequest) {
        User userQuery = new User();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
        List<User> userList = userService.list(queryWrapper);
        List<UserVO> userVOList = userList.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(userVOList);
    }

    @GetMapping("/list/page")
    public BaseResponse listUserByPage(UserQueryRequest userQueryRequest, HttpServletRequest request) {
        long current = 1;
        long size = 10;
        User userQuery = new User();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
            current = userQueryRequest.getCurrent();
            size = userQueryRequest.getPageSize();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
        Page<User> userPage = userService.page(new Page<>(current, size), queryWrapper);
        Page<UserVO> userVOPage = new PageDTO<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserVO> userVOList = userPage.getRecords().stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }
    // endregion
}
