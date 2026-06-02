package com.mini.batis.session;

import com.mini.batis.core.XMLConfigBuilder;
import com.mini.batis.core.XMLConfigBuilderV2;
import com.mini.batis.model.Configuration;

public class SqlSessionFactoryBuilderV2 {

    public SqlSessionFactory build(String configPath) {
        XMLConfigBuilderV2 xmlConfigBuilder = new XMLConfigBuilderV2();
        Configuration configuration = xmlConfigBuilder.parse(configPath);
        return new DefaultSqlSessionFactory(configuration);
    }
}
