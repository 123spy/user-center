package com.spy.usercenterbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spy.usercenterbackend.modal.entity.user.User;
import org.springframework.stereotype.Repository;

/**
 * UserMapper接口
 */
@Repository
public interface UserMapper extends BaseMapper<User> {
}
