package com.mini.batis.objects;

import com.mini.batis.session.SqlSession;
import com.mini.batis.session.SqlSessionFactory;
import com.mini.batis.session.SqlSessionFactoryBuilder;
import com.mini.batis.session.SqlSessionFactoryBuilderV2;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReadSqlMapObjectV7Demo {
    public static void main(String[] args) {
        String statementId = "com.mini.batis.mapper.UserMapper.insertUser";
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("username", "jim");
        params.put("password", "password777");
        params.put("email", "jim@example.com");

        SqlSessionFactoryBuilderV2 factoryBuilder = new SqlSessionFactoryBuilderV2();
        SqlSessionFactory sqlSessionFactory = factoryBuilder.build("sqlMapConfig.xml");
        SqlSession sqlSession = sqlSessionFactory.openSession();

        int result = sqlSession.insert(statementId, params);

        System.out.println("effected rows =: " + result);
    }
}
