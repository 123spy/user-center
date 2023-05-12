package com.spy.usercenterbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.spy.usercenterbackend.annotation.AuthCheck;
import com.spy.usercenterbackend.common.BaseResponse;
import com.spy.usercenterbackend.common.ErrorCode;
import com.spy.usercenterbackend.common.ResultUtils;
import com.spy.usercenterbackend.constant.UserConstant;
import com.spy.usercenterbackend.exception.BusinessException;
import com.spy.usercenterbackend.modal.dto.user.*;
import com.spy.usercenterbackend.modal.entity.user.User;
import com.spy.usercenterbackend.modal.vo.user.UserVO;
import com.spy.usercenterbackend.service.UserService;
import com.spy.usercenterbackend.utils.BaseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.spy.usercenterbackend.constant.UserConstant.*;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    // region 登陆相关

    /**
     * 用户注册
     * @param userRegisterRequest
     * @param request
     * @return id
     */
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

        if (!BaseUtil.checkAccount(userAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号格式错误");
        }

        if(!BaseUtil.checkPassword(userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码格式错误");
        }

        Long id = userService.userRegister(userAccount, userPassword);
        return ResultUtils.success(id);
    }

    /**
     * 用户登录
     * @param userLoginRequest
     * @param request
     * @return bool
     */
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
        if (!BaseUtil.checkAccount(userAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号格式错误");
        }

        if(!BaseUtil.checkPassword(userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码格式错误");
        }

        boolean login = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(login);
    }

    /**
     * 用户退出
     * @param request
     * @return bool
     */
    @GetMapping("/logout")
    public BaseResponse userLogout(HttpServletRequest request) {
        if(request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean logout = userService.userLogout(request);
        return ResultUtils.success(logout);
    }

    /**
     * 获取当前登录用户
     * @param request
     * @return UserVO
     */
    @GetMapping("/current")
    public BaseResponse getCurrentUser(HttpServletRequest request) {
        if(request == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        User user = userService.getCurrentUser(request);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }
    // endregion

    // region 增删改查

    /**
     * 获取用户
     * @param id
     * @return UserVO
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = ADMIN_ROLE)
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

    /**
     * 删除用户
     * @param id
     * @return bool
     */
    @GetMapping("/delete")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse deleteUser(Long id) {
        if(id < 0) {
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

    /**
     * 添加用户
     * @param userAddRequest
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse addUser(@RequestBody UserAddRequest userAddRequest) {
        synchronized (userAddRequest.getUserAccount().intern()) {
            if (userAddRequest == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }

            User user = new User();

            // 校验每个数据是否符合
            // 1. 账号
            String userAccount = userAddRequest.getUserAccount();
            if (!BaseUtil.checkAccount(userAccount)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号格式错误");
            }
            // 校验该账号是否有人注册
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("userAccount", userAccount);
            long count = userService.count(userQueryWrapper);
            if (count != 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "该账号已被注册");
            }
            user.setUserAccount(userAccount);

            // 2. 用户名
            String username = userAddRequest.getUsername();
            if (StringUtils.isBlank(username)) {
                // 如果用户名为空，则随机生成用户名
                username = BaseUtil.randomUsername();
            } else {
                if (!BaseUtil.checkUsername(username)) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名格式错误");
                } else {
                    user.setUsername(username);
                }
            }

            String avatarUrl = userAddRequest.getAvatarUrl();
            if (StringUtils.isNotBlank(avatarUrl)) {
                user.setAvatarUrl(avatarUrl);
            }

            String userPassword = userAddRequest.getUserPassword();
            if (!BaseUtil.checkPassword(userPassword)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码格式错误");
            }
            user.setUserPassword(userPassword);

            String phone = userAddRequest.getPhone();
            if (StringUtils.isNotBlank(phone)) {
                // 添加用户的电话不为空，则校验电话号
                if (!BaseUtil.checkPhone(phone)) {
                    // 电话格式错误
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "电话格式错误");
                } else {
                    // 格式正确
                    user.setPhone(phone);
                }
            } else {
                user.setPhone(null);
            }


            String email = userAddRequest.getEmail();
            if (StringUtils.isNotBlank(email)) {
                // 添加用户的电话不为空，则校验电话号
                if (!BaseUtil.checkEmail(email)) {
                    // 电话格式错误
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式错误");
                } else {
                    // 格式正确
                    user.setEmail(email);
                }
            } else {
                user.setEmail(null);
            }

            String userRole = userAddRequest.getUserRole();
            if (StringUtils.isBlank(userRole)) {
                // 添加用户身份为空，则设置为普通用户
                user.setUserRole(DEFAULT_ROLE);
            } else {
                if (BaseUtil.checkUserRole(userRole)) {
                    user.setUserRole(userRole);
                } else {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户身份格式错误");
                }
            }

            Integer userStatus = userAddRequest.getUserStatus();
            if (userStatus == null) {
                // 添加用户状态为空，则设置为正常用户
                user.setUserStatus(0);
            } else {
                if (userStatus != 1 && userStatus != 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户状态格式错误");
                } else {
                    user.setUserStatus(userStatus);
                }
            }

            boolean save = userService.save(user);
            if (!save) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据库异常,添加失败");
            }
            return ResultUtils.success(save);
        }
    }

    /**
     * 用户更新
     * @param userUpdateRequest
     * @return bool
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验是否存在用户, 在这里我们可以加锁，防止多人用户导致的线程问题，但是因为需要的不多，暂时放弃加锁
        User oldUser = userService.getById(userUpdateRequest.getId());
        if(oldUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User newUser = new User();
        newUser.setId(userUpdateRequest.getId());

        // 校验
        // 1. 账号
        String userAccount = userUpdateRequest.getUserAccount();

        if(StringUtils.isNotBlank(userAccount)) {
            if (BaseUtil.checkAccount(userAccount)) {
                // 存在，且正常，校验该账号是否有人注册
                QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
                userQueryWrapper.eq("userAccount", userAccount);
                long count = userService.count(userQueryWrapper);
                if (count != 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "该账号已被注册");
                }
                newUser.setUserAccount(userAccount);
            } else {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号格式错误");
            }
        }

        // 2. 用户名
        String username = userUpdateRequest.getUsername();
        if(StringUtils.isNotBlank(username)) {
            if(BaseUtil.checkUsername(username)) {
                newUser.setUsername(username);
            } else {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名格式错误");
            }
        }

        // 用户头像
        String avatarUrl = userUpdateRequest.getAvatarUrl();
        if (StringUtils.isNotBlank(avatarUrl)) {
            newUser.setAvatarUrl(avatarUrl);
        }

        // 用户密码
        String userPassword = userUpdateRequest.getUserPassword();
        if(StringUtils.isNotBlank(userPassword)) {
            // 如果存在
            if(BaseUtil.checkPassword(userPassword)) {
                newUser.setUserPassword(userPassword);
            } else {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码格式错误");
            }
        }


        String phone = userUpdateRequest.getPhone();
        if (StringUtils.isNotBlank(phone)) {
            // 添加用户的电话不为空，则校验电话号
            if (BaseUtil.checkPhone(phone)) {
                // 格式正确
                newUser.setPhone(phone);
            } else {
                // 电话格式错误
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "电话格式错误");
            }
        }


        String email = userUpdateRequest.getEmail();
        if (StringUtils.isNotBlank(email)) {
            // 添加用户的电话不为空，则校验电话号
            if (BaseUtil.checkEmail(email)) {
                // 格式正确
                newUser.setEmail(email);
            } else {
                // 电话格式错误
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式错误");
            }
        }

        String userRole = userUpdateRequest.getUserRole();
        if (StringUtils.isNotBlank(userRole)) {
            if (BaseUtil.checkUserRole(userRole)) {
                newUser.setUserRole(userRole);
            } else {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户身份格式错误");
            }
        }

        Integer userStatus = userUpdateRequest.getUserStatus();
        if (userStatus != null) {
            if (userStatus != 1 && userStatus != 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户状态格式错误");
            } else {
                newUser.setUserStatus(userStatus);
            }
        }


        boolean result = userService.updateById(newUser);
        return ResultUtils.success(result);
    }

    /**
     * 用户列表查询
     * @param userQueryRequest
     * @return List<UserVo>
     */
    @GetMapping("/list")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse listUser(UserQueryRequest userQueryRequest) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();

        if(userQueryRequest != null) {
            // id
            Long id = userQueryRequest.getId();
            if(id != null) {
                userQueryWrapper.eq("id", id);
            }

            // 用户账号
            String userAccount = userQueryRequest.getUserAccount();
            if(StringUtils.isNotBlank(userAccount)) {
                if(BaseUtil.checkAccount(userAccount)) {
                    userQueryWrapper.eq("userAccount", userAccount);
                } else {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号格式错误");
                }
            }

            // 用户名
            String username = userQueryRequest.getUsername();
            if(StringUtils.isNotBlank(username)) {
                userQueryWrapper.like("username", username);
            }

            // 用户头像
            String avatarUrl = userQueryRequest.getAvatarUrl();
            if(StringUtils.isNotBlank(avatarUrl)) {
                userQueryWrapper.like("avatarUrl", avatarUrl);
            }

            // 用户密码
            String userPassword = userQueryRequest.getUserPassword();
            if(StringUtils.isNotBlank(userPassword)) {
                if(BaseUtil.checkPassword(userPassword)) {
                    userQueryWrapper.eq("userPassword", userPassword);
                } else {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码格式错误");
                }
            }

            // 用户手机
            String phone = userQueryRequest.getPhone();
            if(StringUtils.isNotBlank(phone)) {
                if(BaseUtil.checkPhone(phone)) {
                    userQueryWrapper.eq("phone", phone);
                } else {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机格式错误");
                }
            }

            // 邮箱
            String email = userQueryRequest.getEmail();
            if(StringUtils.isNotBlank(email)) {
                if(BaseUtil.checkEmail(email)) {
                    userQueryWrapper.eq("email", email);
                } else {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式错误");
                }
            }

            // 用户身份
            String userRole = userQueryRequest.getUserRole();
            if(StringUtils.isNotBlank(userRole)) {
                if(BaseUtil.checkUserRole(userRole)) {
                    userQueryWrapper.eq("userRole", userRole);
                } else {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户身份格式错误");
                }
            }

            // 用户状态
            Integer userStatus = userQueryRequest.getUserStatus();
            if(userStatus != null) {
                if(userStatus != 0 && userStatus != 1) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户状态格式错误");
                } else {
                    userQueryWrapper.eq("userStatus", userStatus);
                }
            }
        }

        List<User> userList = userService.list(userQueryWrapper);
        List<UserVO> userVOList = userList.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(userVOList);
    }

    /**
     * 查询用户页表
     * @param userQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse listUserByPage(UserQueryRequest userQueryRequest, HttpServletRequest request) {
        long current = 1;
        long size = 10;

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();

        if (userQueryRequest != null) {
            current = userQueryRequest.getCurrent();
            size = userQueryRequest.getPageSize();

            // id
            Long id = userQueryRequest.getId();
            if(id != null) {
                userQueryWrapper.eq("id", id);
            }

            // 用户账号
            String userAccount = userQueryRequest.getUserAccount();
            if(StringUtils.isNotBlank(userAccount)) {
                if(BaseUtil.checkAccount(userAccount)) {
                    userQueryWrapper.eq("userAccount", userAccount);
                } else {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号格式错误");
                }
            }

            // 用户名
            String username = userQueryRequest.getUsername();
            if(StringUtils.isNotBlank(username)) {
                userQueryWrapper.like("username", username);
            }

            // 用户头像
            String avatarUrl = userQueryRequest.getAvatarUrl();
            if(StringUtils.isNotBlank(avatarUrl)) {
                userQueryWrapper.like("avatarUrl", avatarUrl);
            }

            // 用户密码
            String userPassword = userQueryRequest.getUserPassword();
            if(StringUtils.isNotBlank(userPassword)) {
                if(BaseUtil.checkPassword(userPassword)) {
                    userQueryWrapper.eq("userPassword", userPassword);
                } else {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码格式错误");
                }
            }

            // 用户手机
            String phone = userQueryRequest.getPhone();
            if(StringUtils.isNotBlank(phone)) {
                if(BaseUtil.checkPhone(phone)) {
                    userQueryWrapper.eq("phone", phone);
                } else {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机格式错误");
                }
            }

            // 邮箱
            String email = userQueryRequest.getEmail();
            if(StringUtils.isNotBlank(email)) {
                if(BaseUtil.checkEmail(email)) {
                    userQueryWrapper.eq("email", email);
                } else {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式错误");
                }
            }

            // 用户身份
            String userRole = userQueryRequest.getUserRole();
            if(StringUtils.isNotBlank(userRole)) {
                if(BaseUtil.checkUserRole(userRole)) {
                    userQueryWrapper.eq("userRole", userRole);
                } else {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户身份格式错误");
                }
            }

            // 用户状态
            Integer userStatus = userQueryRequest.getUserStatus();
            if(userStatus != null) {
                if(userStatus != 0 && userStatus != 1) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户状态格式错误");
                } else {
                    userQueryWrapper.eq("userStatus", userStatus);
                }
            }

        }

        Page<User> userPage = userService.page(new Page<>(current, size), userQueryWrapper);
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
