package com.mini.batis.objects;

import com.mini.batis.entity.User;
import com.mini.batis.entity.UserQuery;
import com.mini.batis.mapper.UserMapper;
import com.mini.batis.session.SqlSession;
import com.mini.batis.session.SqlSessionFactory;
import com.mini.batis.session.SqlSessionFactoryBuilder;

import java.util.List;

public class ReadSqlMapObjectDemo {

    public static void main(String[] args) {
        SqlSessionFactoryBuilder factoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = factoryBuilder.build("sqlMapConfig.xml");
        SqlSession sqlSession = sqlSessionFactory.openSession();

        User user = new User();
        user.setUsername("Bom");
        user.setPassword("password999");
        user.setEmail("Bom@example.com");

        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        int affectedRows = userMapper.insertUser(user);
        System.out.println("affected rows =: " + affectedRows);

        UserQuery userQuery = new UserQuery();
        userQuery.setUsername("Bom");
        userQuery.setId(10);

        User userByName = userMapper.findByUsername(userQuery);
        System.out.println("userByName =:" + userByName);

        List<User> userList = userMapper.findAll();
        userList.forEach(System.out::println);

    }
}
