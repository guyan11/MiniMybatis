package com.mini.batis.scripting.defaults;

import com.mini.batis.scripting.BoundSql;
import com.mini.batis.scripting.SqlSource;
import com.mini.batis.scripting.SqlSourceBuilder;
import com.mini.batis.scripting.xmltags.DynamicContext;
import com.mini.batis.scripting.xmltags.SqlNode;

public class DynamicSqlSource implements SqlSource {

    SqlNode rootSqlNode;

    public DynamicSqlSource(SqlNode rootSqlNode) {
        this.rootSqlNode = rootSqlNode;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        DynamicContext context = new DynamicContext(parameterObject);
        rootSqlNode.apply(context);

        SqlSourceBuilder sqlSourceBuilder = new SqlSourceBuilder();
        SqlSource sqlSource = sqlSourceBuilder.parse(context.getSql());
        return sqlSource.getBoundSql(parameterObject);
    }
}
