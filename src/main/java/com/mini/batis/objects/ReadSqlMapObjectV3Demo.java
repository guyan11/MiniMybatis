package com.mini.batis.objects;

import com.mini.batis.core.XMLConfigBuilder;
import com.mini.batis.executor.Executor;
import com.mini.batis.executor.SimpleExecutor;
import com.mini.batis.model.Configuration;
import com.mini.batis.model.MapperStatement;
import org.apache.commons.dbcp.BasicDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReadSqlMapObjectV3Demo {

    public static void main(String[] args) throws SQLException {
        String configPath = "sqlMapConfig.xml";
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder();
        Configuration configuration = xmlConfigBuilder.parse(configPath);
        if (configuration == null) {
            throw new RuntimeException("configuration is null");
        }

        String statementId = "com.mini.batis.mapper.UserMapper.findByUsername";
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("username", "jane");
        params.put("id", 2);

        MapperStatement statementInfo = configuration.getMapperStatement(statementId);

        if (null == statementInfo) {
            throw new RuntimeException("Can not find statement: " + statementId);
        }

        Executor executor = new SimpleExecutor(configuration);
        List<Object> resList = executor.query(statementInfo, params);
        System.out.println("resList =: ");
        resList.forEach(System.out::println);

        DataSource dataSource = configuration.getDataSource();
        if (dataSource instanceof BasicDataSource) {
            ((BasicDataSource) dataSource).close();
        }

    }

}
