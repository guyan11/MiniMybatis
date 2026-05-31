package com.mini.batis.objects;

import com.mini.batis.entity.User;
import com.mini.batis.session.DefaultSqlSession;
import com.mini.batis.session.SqlSession;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReadSqlMapObjectV5Demo {
    public static void main(String[] args) throws Exception {
        String statementId = "com.mini.batis.mapper.UserMapper.findByUsername";
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("username", "jane");
        params.put("id", 2);

        SqlSession sqlSession = new DefaultSqlSession();
        List<Object> resList = sqlSession.selectList(statementId, params);

        System.out.println("resList =: ");
        resList.forEach(System.out::println);

        Object o = sqlSession.selectOne(statementId, params);
        if (o instanceof User) {
            System.out.println("User =: " + o);
        }
    }
}
