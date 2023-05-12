package com.spy.usercenterbackend.modal.dto.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;

@Data
public class UserAddRequest {
    private String userAccount;

    private String username;

    private String avatarUrl;

    private String userPassword;

    private String phone;

    private String email;

    private Integer userRole;

    private Integer userStatus;
}
