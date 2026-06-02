package com.mini.batis.executor;

import com.mini.batis.model.Configuration;
import com.mini.batis.model.MapperStatement;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SimpleExecutorV2 implements Executor {

    private final Configuration configuration;

    public SimpleExecutorV2(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> query(MapperStatement mapperStatement, Object parameter) {
        Connection connection = null;
        try {
            connection = configuration.getDataSource().getConnection();
            StatementHandler statementHandler = new SimpleStatementHandlerV2(mapperStatement, parameter);
            return statementHandler.query(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException("error closing connection", e);
                }
            }
        }
    }

    @Override
    public int update(MapperStatement statementInfo, Object parameter) {
        Connection connection = null;
        try {
            connection = configuration.getDataSource().getConnection();
            StatementHandler statementHandler = new SimpleStatementHandlerV2(statementInfo, parameter);
            return statementHandler.update(connection);
        } catch (Exception e) {
            throw new RuntimeException("Error executing update data" + statementInfo.getId(), e);
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
