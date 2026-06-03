package com.mini.batis.objects;

import com.mini.batis.entity.User;
import com.mini.batis.session.SqlSession;
import com.mini.batis.session.SqlSessionFactory;
import com.mini.batis.session.SqlSessionFactoryBuilder;

public class ReadSqlMapObjectDemo {

    public static void main(String[] args) {
        SqlSessionFactoryBuilder factoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = factoryBuilder.build("sqlMapConfig.xml");
        SqlSession sqlSession = sqlSessionFactory.openSession();

        User user = new User();
        user.setUsername("Nick");
        user.setPassword("password999");
        user.setEmail("Nick@example.com");

        int result = sqlSession.insert("com.mini.batis.mapper.UserMapper.insertUser", user);
        System.out.println("affected rows =: " + result);
    }
}
