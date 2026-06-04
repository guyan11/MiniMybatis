package com.mini.batis.mapper;

import com.mini.batis.entity.User;
import com.mini.batis.entity.UserQuery;

import java.util.List;

public interface UserMapper {

    List<User> findAll();

    User findById(Integer id);

    User findByName(UserQuery userQuery);

    int insertUser(User user);
}
