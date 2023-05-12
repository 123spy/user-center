package com.spy.usercenterbackend.modal.vo;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;

@Data
public class UserVO {
    private Long id;

    private String userAccount;

    private String username;

    private String avatarUrl;

    private String phone;

    private String email;

    private Integer userRole;

    private Integer userStatus;

    private Date createTime;

    private Date updateTime;
}
