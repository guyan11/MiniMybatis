package com.mini.batis.session;

import com.mini.batis.binding.MapperProxyFactory;
import com.mini.batis.executor.Executor;
import com.mini.batis.executor.SimpleExecutor;
import com.mini.batis.model.Configuration;
import com.mini.batis.model.MapperStatement;

import java.util.List;

public class DefaultSqlSession implements SqlSession {

    private final Configuration configuration;
    private final Executor executor;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
        this.executor = new SimpleExecutor(configuration);
    }

    @Override
    public <E> List<E> selectList(String statementId, Object parameter) {
        MapperStatement mapperStatement = getMapperStatement(statementId);
        return executor.query(mapperStatement, parameter);
    }

    @Override
    public <E> E selectOne(String statementId, Object parameter) {
        List<E> list = selectList(statementId, parameter);
        if (list == null || list.isEmpty()) {
            return null;
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        throw new RuntimeException("expected one result but got more than one");
    }

    @Override
    public int insert(String statementId, Object parameter) {
        MapperStatement mapperStatement = getMapperStatement(statementId);
        if (!"insert".equalsIgnoreCase(mapperStatement.getSqlCommandType())) {
            throw new RuntimeException("Statement is not insert: " + statementId);
        }
        return executor.update(mapperStatement, parameter);
    }

    private MapperStatement getMapperStatement(String statementId) {
        MapperStatement mapperStatement = configuration.getMapperStatement(statementId);
        if (mapperStatement == null) {
            throw new RuntimeException("Can not find statement: " + statementId);
        }
        return mapperStatement;
    }

    @Override
    public <T> T getMapper(Class<T> mapperClass) {
        MapperProxyFactory<T> proxyFactory = new MapperProxyFactory<>(mapperClass);
        return proxyFactory.newInstance(this);
    }
}
