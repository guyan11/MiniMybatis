package com.mini.batis.session;

import com.mini.batis.executor.Executor;
import com.mini.batis.executor.SimpleExecutor;
import com.mini.batis.model.Configuration;
import com.mini.batis.model.MapperStatement;

import java.util.List;

public class DefaultSqlSessionV2 implements SqlSession {

    private final Configuration configuration;

    public DefaultSqlSessionV2(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> selectList(String statementId, Object parameter) {
        Executor executor = new SimpleExecutor(configuration);
        MapperStatement statementInfo = configuration.getMapperStatement(statementId);
        if (statementInfo == null) {
            throw new RuntimeException("statementInfo is null");
        }
        return executor.query(statementInfo, parameter);
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

}
