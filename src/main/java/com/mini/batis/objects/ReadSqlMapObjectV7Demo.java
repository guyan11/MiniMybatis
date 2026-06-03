package com.mini.batis.objects;

import com.mini.batis.entity.User;
import com.mini.batis.session.SqlSession;
import com.mini.batis.session.SqlSessionFactory;
import com.mini.batis.session.SqlSessionFactoryBuilderV2;

public class ReadSqlMapObjectV7Demo {
    public static void main(String[] args) {
        String statementId = "com.mini.batis.mapper.UserMapper.insertUser";
        // Map<String, Object> params = new LinkedHashMap<>();
        // params.put("username", "Anna");
        // params.put("password", "password666");
        // params.put("email", "Anna@example.com");
        User user = new User();
        user.setUsername("Anna");
        user.setPassword("password666");
        user.setEmail("Anna@example.com");

        SqlSessionFactoryBuilderV2 factoryBuilder = new SqlSessionFactoryBuilderV2();
        SqlSessionFactory sqlSessionFactory = factoryBuilder.build("sqlMapConfig.xml");
        SqlSession sqlSession = sqlSessionFactory.openSession();

        int result = sqlSession.insert(statementId, user);

        System.out.println("effected rows =: " + result);
    }
}
