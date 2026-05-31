package com.mini.batis.session;

import com.mini.batis.model.Configuration;
import org.apache.commons.dbcp.BasicDataSource;

public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSessionV2(configuration);
    }

    @Override
    public void closeSession() throws Exception {
        ((BasicDataSource) configuration.getDataSource()).close();
    }
}
