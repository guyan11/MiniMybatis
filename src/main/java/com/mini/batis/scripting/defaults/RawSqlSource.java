package com.mini.batis.scripting.defaults;

import com.mini.batis.scripting.BoundSql;
import com.mini.batis.scripting.SqlSource;
import com.mini.batis.scripting.SqlSourceBuilder;
import com.mini.batis.scripting.xmltags.DynamicContext;
import com.mini.batis.scripting.xmltags.SqlNode;

public class RawSqlSource implements SqlSource {

    private final SqlSource sqlSource;


    public RawSqlSource(SqlNode rootSqlNode) {
        DynamicContext context = new DynamicContext(null);
        rootSqlNode.apply(context);
        String sql = context.getSql();
        SqlSourceBuilder sqlSourceBuilder = new SqlSourceBuilder();
        this.sqlSource = sqlSourceBuilder.parse(sql);
    }


    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return sqlSource.getBoundSql(parameterObject);
    }
}
