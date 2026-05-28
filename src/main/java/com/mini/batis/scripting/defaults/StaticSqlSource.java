package com.mini.batis.scripting.defaults;

import com.mini.batis.scripting.BoundSql;
import com.mini.batis.scripting.ParameterMapping;
import com.mini.batis.scripting.SqlSource;

import java.util.List;


public class StaticSqlSource implements SqlSource {

    private final String sql;

    private final List<ParameterMapping> parameterMappings;


    public StaticSqlSource(String sql, List<ParameterMapping> parameterMappings) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return new BoundSql(sql, parameterMappings, parameterObject);
    }
}
