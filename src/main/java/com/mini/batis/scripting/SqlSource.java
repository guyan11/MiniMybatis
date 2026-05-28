package com.mini.batis.scripting;

public interface SqlSource {

    BoundSql getBoundSql(Object parameterObject);
}
