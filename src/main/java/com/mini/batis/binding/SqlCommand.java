package com.mini.batis.binding;

import com.mini.batis.model.MapperStatement;
import com.mini.batis.session.SqlSession;
import lombok.Data;

import java.lang.reflect.Method;

@Data
public class SqlCommand {
    private final String statementId;

    private final String sqlCommandType;

    public SqlCommand(Class<?> mapperInterface, Method method, SqlSession sqlSession) {
        this.statementId = mapperInterface.getName() + "." + method.getName();
        MapperStatement mapperStatement = sqlSession.getMapperStatement(statementId);
        this.sqlCommandType = mapperStatement.getSqlCommandType();
    }

}