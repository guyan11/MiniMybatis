package com.mini.batis.executor;

import com.mini.batis.reflection.ParamValueResolver;
import com.mini.batis.scripting.BoundSql;
import com.mini.batis.scripting.ParameterMapping;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DefaultParameterHandler implements ParameterHandler {

    private final BoundSql boundSql;
    private final Object parameterObject;

    public DefaultParameterHandler(BoundSql boundSql, Object parameterObject) {
        this.boundSql = boundSql;
        this.parameterObject = parameterObject;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        for (int i = 0; i < parameterMappings.size(); i++) {
            ParameterMapping parameterMapping = parameterMappings.get(i);
            String property = parameterMapping.getProperty();
            Object value = ParamValueResolver.getValue(parameterObject, property);
            preparedStatement.setObject(i + 1, value);
        }
    }
}
