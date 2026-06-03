package com.mini.batis.executor;

import com.mini.batis.model.Configuration;
import com.mini.batis.model.MapperStatement;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SimpleExecutor implements Executor {

    private final Configuration configuration;

    public SimpleExecutor(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> query(MapperStatement mapperStatement, Object parameter) {
        Connection connection = null;
        try {
            connection = configuration.getDataSource().getConnection();
            StatementHandler statementHandler = new SimpleStatementHandler(mapperStatement, parameter);
            return statementHandler.query(connection);
        } catch (SQLException e) {
            throw new RuntimeException("Error executing query: " + mapperStatement.getId(), e);
        } finally {
            closeConnection(connection);
        }
    }

    @Override
    public int update(MapperStatement mapperStatement, Object parameter) {
        Connection connection = null;
        try {
            connection = configuration.getDataSource().getConnection();
            StatementHandler statementHandler = new SimpleStatementHandler(mapperStatement, parameter);
            return statementHandler.update(connection);
        } catch (Exception e) {
            throw new RuntimeException("Error executing update: " + mapperStatement.getId(), e);
        } finally {
            closeConnection(connection);
        }
    }

    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException("Error closing connection", e);
            }
        }
    }
}
