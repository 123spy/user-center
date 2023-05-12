package com.spy.usercenterbackend.utils;

import java.util.UUID;
import java.util.regex.Pattern;

import static com.spy.usercenterbackend.constant.UserConstant.ADMIN_ROLE;
import static com.spy.usercenterbackend.constant.UserConstant.DEFAULT_ROLE;

/**
 * 基础工具类
 */
public class BaseUtil {

    /**
     * 用户密码校验
     * @param password
     * @return true 正确， false 失败
     */
    static public Boolean checkPassword(String password) {

        // 密码要求：
        //      1. 长度 >= 4
        //      2. 只支持数字大小写英文与部分特殊符号
        String regex = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]{4,}$";
        return Pattern.matches(regex, password);
    }

    /**
     * 用户账号校验
     * @param account
     * @return true 正确， false 失败
     */
    static public Boolean checkAccount(String account) {
        // 账号要求：
        //      1. 长度 >= 4
        //      2. 只支持数字大小写英文与部分特殊符号
        String regex = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]{4,}$";
        return Pattern.matches(regex, account);
    }

    /**
     * 随机用户名生成
     * @return username
     */
    static public String randomUsername() {
        return "用户" + UUID.randomUUID().toString().replace("-", "").trim().substring(0, 6);
    }

    /**
     * 手机号校验
     * @param phone
     * @return bool
     */
    static public Boolean checkPhone(String phone) {
        String regex = "^1[3-9]\\d{9}$";
        return Pattern.matches(regex, phone);
    }

    /**
     * 邮箱校验
     * @param email
     * @return bool
     */
    static public Boolean checkEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return Pattern.matches(regex, email);
    }

    /**
     * 用户名校验
     * @param username
     * @return
     */
    static public Boolean checkUsername(String username) {
        // 用户名不能为空，长度小于20
        if(username.length() > 20) {
            return false;
        }
        return true;
    }

    /**
     * 用户身份校验
     */
    static public Boolean checkUserRole(String userRole) {
        // 用户名不能为空，长度小于20
        if(!userRole.equals(DEFAULT_ROLE) && !userRole.equals(ADMIN_ROLE)) {
            return false;
        }
        return true;
    }
}
