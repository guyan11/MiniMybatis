package com.mini.batis.scripting.xmltags;

import lombok.Data;

import java.util.Map;

/**
 * 动态上下文
 */
@Data
public class DynamicContext {

    private final StringBuilder sql = new StringBuilder(" ");

    private final Object parameterObject;

    private Map<String, Object> bindings;

    public DynamicContext(Object parameterObject) {
        this.parameterObject = parameterObject;
    }

    public String getSql() {
        return sql.toString().trim();
    }

    public void appendSql(String sql) {
        this.sql.append(sql).append(" ");
    }
}
