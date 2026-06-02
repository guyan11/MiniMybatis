package com.mini.batis.executor;

import com.mini.batis.model.MapperStatement;
import com.mini.batis.reflection.ParamValueResolver;
import com.mini.batis.scripting.BoundSql;
import com.mini.batis.scripting.ParameterMapping;
import com.mini.batis.scripting.SqlSource;
import com.mini.batis.scripting.SqlSourceBuilder;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleStatementHandler implements StatementHandler {

    private final MapperStatement mapperStatement;
    private final Object parameterObject;

    public SimpleStatementHandler(MapperStatement mapperStatement, Object parameterObject) {
        this.mapperStatement = mapperStatement;
        this.parameterObject = parameterObject;
    }

    @Override
    public <E> List<E> query(Connection connection) {
        try {
            BoundSql boundSql = getBoundSql(parameterObject);
            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
            Class<?> resultClass = resolveResultClass(mapperStatement.getResultType());
            if (parameterMappings.isEmpty()) {
                return queryWithStatement(connection, boundSql, resultClass);
            } else {
                return queryWithPrepareStatement(connection, boundSql, resultClass);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error querying statement:" + mapperStatement.getId(), e);
        }
    }

    @Override
    public int update(Connection connection) {
        return 0;
    }

    private <E> List<E> queryWithStatement(Connection connection, BoundSql boundSql,
                                           Class<?> resultClass) throws Exception {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            String sql = boundSql.getSql();
            resultSet = statement.executeQuery(sql);
            return handlerResultSet(resultSet, resultClass);
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
        }
    }

    private <E> List<E> queryWithPrepareStatement(Connection connection, BoundSql boundSql,
                                                  Class<?> resultClass) throws Exception {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            String sql = boundSql.getSql();
            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
            preparedStatement = connection.prepareStatement(sql);
            setParameter(parameterMappings, preparedStatement);

            resultSet = preparedStatement.executeQuery();
            return handlerResultSet(resultSet, resultClass);
        } finally {
            closeStatement(preparedStatement);
            closeResultSet(resultSet);
        }
    }

    @SuppressWarnings("unchecked")
    private <E> List<E> handlerResultSet(ResultSet resultSet, Class<?> resultClass) throws Exception {
        if (resultSet == null) {
            return new ArrayList<>();
        }
        List<E> resultList = new ArrayList<>();
        while (resultSet.next()) {
            Object object = mapToObject(resultSet, resultClass);
            resultList.add((E) object);
        }
        return resultList;
    }

    private static Object mapToObject(ResultSet resultSet, Class<?> resultClass) throws InstantiationException, IllegalAccessException, SQLException {
        Object res = resultClass.newInstance();
        Field[] declaredFields = res.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Object value = resultSet.getObject(declaredField.getName());
            if (null == value) {
                continue;
            }
            declaredField.setAccessible(true);
            declaredField.set(res, value);
        }
        return res;
    }

    private void setParameter(List<ParameterMapping> parameterMappings, PreparedStatement preparedStatement) throws SQLException {
        for (int i = 0; i < parameterMappings.size(); i++) {
            ParameterMapping parameterMapping = parameterMappings.get(i);
            String propertyName = parameterMapping.getProperty();
            Object value = ParamValueResolver.getValue(parameterObject, propertyName);
            preparedStatement.setObject(i + 1, value);
        }
    }

    private Class<?> resolveResultClass(String resultType) throws ClassNotFoundException {
        if (null == resultType || resultType.trim().isEmpty()) {
            throw new RuntimeException("resultType is empty:" + mapperStatement.getId());
        }
        return Class.forName(resultType);

    }

    private BoundSql getBoundSql(Object parameterObject) {
        SqlSourceBuilder sqlSourceBuilder = new SqlSourceBuilder();
        String sqlAfterDollarHolder = sqlSourceBuilder.parseDollarPlaceholder(mapperStatement.getSql(), parameterObject);
        SqlSource sqlSource = sqlSourceBuilder.parse(sqlAfterDollarHolder);
        return sqlSource.getBoundSql(parameterObject);
    }


    private void closeStatement(Statement statement) throws Exception {
        if (statement != null) {
            statement.close();
        }
    }

    private void closeResultSet(ResultSet resultSet) throws Exception {
        if (resultSet != null) {
            resultSet.close();
        }
    }
}
