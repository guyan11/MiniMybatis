package com.mini.batis.objects;

import com.mini.batis.session.SqlSession;
import com.mini.batis.session.SqlSessionFactory;
import com.mini.batis.session.SqlSessionFactoryBuilder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReadSqlMapObjectV6Demo {
    public static void main(String[] args) {
        String statementId = "com.mini.batis.mapper.UserMapper.findByUsername";
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("username", "jane");
        params.put("id", 2);

        SqlSessionFactoryBuilder factoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory build = factoryBuilder.build("sqlMapConfig.xml");
        SqlSession sqlSession = build.openSession();

        List<Object> resList = sqlSession.selectList(statementId, params);

        System.out.println("resList =: ");
        resList.forEach(System.out::println);
    }
}
