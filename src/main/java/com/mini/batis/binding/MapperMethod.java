package com.mini.batis.binding;

import com.mini.batis.session.SqlSession;

import java.lang.reflect.Method;

public class MapperMethod {

    private final SqlCommand sqlCommand;

    private final MethodSignature methodSignature;


    public MapperMethod(Class<?> mapperInterface, Method method, SqlSession sqlSession) {
        this.sqlCommand = new SqlCommand(mapperInterface, method, sqlSession);
        this.methodSignature = new MethodSignature(method);
    }

    public Object execute(SqlSession sqlSession, Object[] args) {
        Object parameter = convertArgsToSqlCommandParam(args);
        if ("insert".equalsIgnoreCase(sqlCommand.getSqlCommandType())) {
            return sqlSession.insert(sqlCommand.getStatementId(), parameter);
        }
        if ("select".equalsIgnoreCase(sqlCommand.getSqlCommandType())) {
            if (methodSignature.returnMany()) {
                return sqlSession.selectList(sqlCommand.getStatementId(), parameter);
            }
            return sqlSession.selectOne(sqlCommand.getStatementId(), parameter);
        }
        throw new RuntimeException("Unsupported sql command type: " + sqlCommand.getSqlCommandType());
    }

    public Object convertArgsToSqlCommandParam(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        if (args.length == 1) {
            return args[0];
        }
        throw new RuntimeException("Multiple parameters are not supported yet");
    }
}
