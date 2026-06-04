package com.mini.batis.binding;

import com.mini.batis.session.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MapperProxy<T> implements InvocationHandler {

    private final SqlSession sqlSession;

    private final Class<T> mapperInterface;

    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }


        MapperMethod mapperMethod = new MapperMethod(mapperInterface, method, sqlSession);
        return mapperMethod.execute(sqlSession, args);

        // String statementId = mapperInterface.getName() + "." + method.getName();
        // Object parameter = getParameter(args);
        // Class<?> returnType = method.getReturnType();
        //
        // MapperStatement mapperStatement = sqlSession.getMapperStatement(statementId);
        // String sqlCommandType = mapperStatement.getSqlCommandType();
        // if ("insert".equalsIgnoreCase(sqlCommandType)) {
        //     return sqlSession.insert(statementId, parameter);
        // }
        //
        // if ("select".equalsIgnoreCase(sqlCommandType) && Collection.class.isAssignableFrom(returnType)) {
        //     return sqlSession.selectList(statementId, parameter);
        // }
        //
        // if ("select".equalsIgnoreCase(sqlCommandType)) {
        //     return sqlSession.selectOne(statementId, parameter);
        // }
        //
        // throw new RuntimeException("Unsupported sql command type: " + sqlCommandType);
    }

    private Object getParameter(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }

        if (args.length == 1) {
            return args[0];
        }
        throw new RuntimeException("Multiple parameters are not supported yet");
    }

    // public boolean isInsertMethod(Class<?> returnType) {
    //     return Integer.class.equals(returnType) || int.class.equals(returnType);
    // }
}
