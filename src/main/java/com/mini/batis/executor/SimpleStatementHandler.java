package com.mini.batis.executor;

import com.mini.batis.model.MapperStatement;
import com.mini.batis.scripting.BoundSql;
import com.mini.batis.scripting.ParameterMapping;
import com.mini.batis.scripting.SqlSource;
import com.mini.batis.scripting.SqlSourceBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
            ResultSetHandler resultSetHandler = new DefaultResultSetHandler(resultClass);
            if (parameterMappings.isEmpty()) {
                return queryWithStatement(connection, boundSql, resultSetHandler);
            }
            return queryWithPreparedStatement(connection, boundSql, resultSetHandler);
        } catch (Exception e) {
            throw new RuntimeException("Error querying statement: " + mapperStatement.getId(), e);
        }
    }

    @Override
    public int update(Connection connection) {
        try {
            BoundSql boundSql = getBoundSql(parameterObject);
            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
            if (parameterMappings.isEmpty()) {
                return updateWithStatement(connection, boundSql);
            }
            return updateWithPreparedStatement(connection, boundSql);
        } catch (Exception e) {
            throw new RuntimeException("Error executing update: " + mapperStatement.getId(), e);
        }
    }

    private <E> List<E> queryWithStatement(Connection connection,
                                           BoundSql boundSql,
                                           ResultSetHandler resultSetHandler) throws Exception {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(boundSql.getSql());
            return resultSetHandler.handleResultSet(resultSet);
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
        }
    }

    private <E> List<E> queryWithPreparedStatement(Connection connection,
                                                   BoundSql boundSql,
                                                   ResultSetHandler resultSetHandler) throws Exception {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(boundSql.getSql());
            ParameterHandler parameterHandler = new DefaultParameterHandler(boundSql, parameterObject);
            parameterHandler.setParameter(preparedStatement);
            resultSet = preparedStatement.executeQuery();
            return resultSetHandler.handleResultSet(resultSet);
        } finally {
            closeResultSet(resultSet);
            closeStatement(preparedStatement);
        }
    }

    private int updateWithStatement(Connection connection, BoundSql boundSql) throws Exception {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            return statement.executeUpdate(boundSql.getSql());
        } finally {
            closeStatement(statement);
        }
    }

    private int updateWithPreparedStatement(Connection connection, BoundSql boundSql) throws Exception {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(boundSql.getSql());
            ParameterHandler parameterHandler = new DefaultParameterHandler(boundSql, parameterObject);
            parameterHandler.setParameter(preparedStatement);
            return preparedStatement.executeUpdate();
        } finally {
            closeStatement(preparedStatement);
        }
    }

    private Class<?> resolveResultClass(String resultType) throws ClassNotFoundException {
        if (resultType == null || resultType.trim().isEmpty()) {
            throw new RuntimeException("resultType is empty: " + mapperStatement.getId());
        }
        return Class.forName(resultType);
    }

    private BoundSql getBoundSql(Object parameterObject) {
        // SqlSourceBuilder sqlSourceBuilder = new SqlSourceBuilder();
        // String sqlAfterDollarParsed = sqlSourceBuilder.parseDollarPlaceholder(mapperStatement.getSql(), parameterObject);
        // SqlSource sqlSource = sqlSourceBuilder.parse(sqlAfterDollarParsed);
        // return sqlSource.getBoundSql(parameterObject);
        return mapperStatement.getSqlSource().getBoundSql(parameterObject);
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
