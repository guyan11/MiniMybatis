package com.mini.batis.scripting.defaults;

import com.mini.batis.scripting.BoundSql;
import com.mini.batis.scripting.SqlSource;
import com.mini.batis.scripting.SqlSourceBuilder;

public class RawSqlSource implements SqlSource {

    private final SqlSource sqlSource;


    public RawSqlSource(String sql) {
        SqlSourceBuilder sqlSourceBuilder = new SqlSourceBuilder();
        this.sqlSource = sqlSourceBuilder.parse(sql);
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return sqlSource.getBoundSql(parameterObject);
    }
}
