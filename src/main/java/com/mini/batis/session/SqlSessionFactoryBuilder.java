package com.mini.batis.session;

import com.mini.batis.core.XMLConfigBuilder;
import com.mini.batis.model.Configuration;

public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(String configPath) {
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder();
        Configuration configuration = xmlConfigBuilder.parse(configPath);
        return new DefaultSqlSessionFactory(configuration);
    }
}
