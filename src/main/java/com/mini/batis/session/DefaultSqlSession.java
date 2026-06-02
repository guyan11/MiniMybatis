package com.mini.batis.session;

import com.mini.batis.core.XMLConfigBuilder;
import com.mini.batis.executor.Executor;
import com.mini.batis.executor.SimpleExecutor;
import com.mini.batis.model.Configuration;
import com.mini.batis.model.MapperStatement;
import org.apache.commons.dbcp.BasicDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

public class DefaultSqlSession implements SqlSession {

    public Configuration initConfiguration() {
        String configPath = "sqlMapConfig.xml";
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder();
        Configuration configuration = xmlConfigBuilder.parse(configPath);
        if (configuration == null) {
            throw new RuntimeException("configuration is null");
        }
        return configuration;
    }

    @Override
    public <E> List<E> selectList(String statementId, Object parameter) {
        Configuration configuration = initConfiguration();
        Executor executor = new SimpleExecutor(configuration);
        MapperStatement statementInfo = configuration.getMapperStatement(statementId);
        List<E> resList = executor.query(statementInfo, parameter);
        closeDataSource(configuration);
        return resList;
    }

    private void closeDataSource(Configuration configuration) {
        DataSource dataSource = configuration.getDataSource();
        if (dataSource instanceof BasicDataSource) {
            try {
                ((BasicDataSource) dataSource).close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public <E> E selectOne(String statementId, Object parameter) {
        List<E> list = selectList(statementId, parameter);
        if (list == null || list.isEmpty()) {
            return null;
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        throw new RuntimeException("expected one result but got more than one");
    }

    @Override
    public int insert(String statementId, Object parameter) {
        return 0;
    }

}
