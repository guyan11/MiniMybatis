package com.mini.batis.model;

import lombok.Data;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Data
public class Configuration {

    private DataSource dataSource;

    private Map<String, MapperStatement> mapperStatementMap = new HashMap<>();

    public void addMapperStatement(String namespace, MapperStatement mapperStatement) {
        mapperStatementMap.put(namespace + "." + mapperStatement.getId(), mapperStatement);
    }

    public MapperStatement getMapperStatement(String statementId) {
        return mapperStatementMap.get(statementId);
    }
}
