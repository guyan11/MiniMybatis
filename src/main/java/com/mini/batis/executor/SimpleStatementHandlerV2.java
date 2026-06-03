package com.mini.batis.executor;

import com.mini.batis.model.MapperStatement;
import com.mini.batis.scripting.BoundSql;
import com.mini.batis.scripting.ParameterMapping;
import com.mini.batis.scripting.SqlSource;
import com.mini.batis.scripting.SqlSourceBuilder;

import java.sql.*;
import java.util.List;

public class SimpleStatementHandlerV2 implements StatementHandler {

    private final MapperStatement mapperStatement;
    private final Object parameterObject;

    public SimpleStatementHandlerV2(MapperStatement mapperStatement, Object parameterObject) {
        this.mapperStatement = mapperStatement;
        this.parameterObject = parameterObject;
    }

    @Override
    public <E> List<E> query(Connection connection) {
        try {
            BoundSql boundSql = getBoundSql(parameterObject);
            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
            Class<?> resultClass = resolveResultClass(mapperStatement.getResultType());
            DefaultResultSetHandler defaultResultSetHandler = new DefaultResultSetHandler(resultClass);
            if (parameterMappings.isEmpty()) {
                return queryWithStatement(connection, boundSql, defaultResultSetHandler);
            } else {
                return queryWithPrepareStatement(connection, boundSql, defaultResultSetHandler);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error querying statement:" + mapperStatement.getId(), e);
        }
    }

    @Override
    public int update(Connection connection) {
        BoundSql boundSql = getBoundSql(parameterObject);
        try {
            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
            if (parameterMappings.isEmpty()) {
                return updateWithStatement(connection, boundSql);
            } else {
                return updateWithPrepareStatement(connection, boundSql);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error executing update data" + mapperStatement.getId(), e);
        }
    }

    private int updateWithPrepareStatement(Connection connection, BoundSql boundSql) throws Exception {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(boundSql.getSql());
            DefaultParameterHandler parameterHandler = new DefaultParameterHandler(boundSql, parameterObject);
            parameterHandler.setParameter(preparedStatement);
            return preparedStatement.executeUpdate();
        } finally {
            closeStatement(preparedStatement);
        }
    }

    private int updateWithStatement(Connection connection, BoundSql boundSql) throws Exception {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            String sql = boundSql.getSql();
            return statement.executeUpdate(sql);
        } finally {
            closeStatement(statement);
        }
    }

    private <E> List<E> queryWithStatement(Connection connection, BoundSql boundSql,
                                           ResultSetHandler resultSetHandler) throws Exception {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            String sql = boundSql.getSql();
            resultSet = statement.executeQuery(sql);
            return resultSetHandler.handleResultSet(resultSet);
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
        }
    }

    private <E> List<E> queryWithPrepareStatement(Connection connection, BoundSql boundSql,
                                                  ResultSetHandler resultSetHandler) throws Exception {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            String sql = boundSql.getSql();
            preparedStatement = connection.prepareStatement(sql);

            DefaultParameterHandler parameterHandler = new DefaultParameterHandler(boundSql, parameterObject);
            parameterHandler.setParameter(preparedStatement);

            resultSet = preparedStatement.executeQuery();
            return resultSetHandler.handleResultSet(resultSet);
        } finally {
            closeResultSet(resultSet);
            closeStatement(preparedStatement);
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
