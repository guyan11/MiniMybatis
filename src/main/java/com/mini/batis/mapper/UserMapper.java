package com.mini.batis.mapper;

import com.mini.batis.entity.User;
import com.mini.batis.entity.UserQuery;

import java.util.List;

public interface UserMapper {

    List<User> findAll();

    User findById(Integer id);

    User findByUsername(UserQuery userQuery);

    int insertUser(User user);

    int updateUser(User user);

    int deleteUser(Integer id);
}
