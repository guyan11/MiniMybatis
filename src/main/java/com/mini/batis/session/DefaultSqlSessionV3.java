package com.mini.batis.session;

import com.mini.batis.executor.Executor;
import com.mini.batis.executor.SimpleExecutor;
import com.mini.batis.executor.SimpleExecutorV2;
import com.mini.batis.model.Configuration;
import com.mini.batis.model.MapperStatement;

import java.util.List;

public class DefaultSqlSessionV3 implements SqlSession {

    private final Configuration configuration;

    private final Executor executor;

    public DefaultSqlSessionV3(Configuration configuration) {
        this.configuration = configuration;
        this.executor = new SimpleExecutorV2(configuration);
    }

    @Override
    public <E> List<E> selectList(String statementId, Object parameter) {
        MapperStatement statementInfo = configuration.getMapperStatement(statementId);
        if (statementInfo == null) {
            throw new RuntimeException("Can not find statement: " + statementId);
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

    @Override
    public int insert(String statementId, Object parameter) {
        MapperStatement statementInfo = configuration.getMapperStatement(statementId);
        if (statementInfo == null) {
            throw new RuntimeException("Can not find statement: " + statementId);
        }
        if (!"insert".equalsIgnoreCase(statementInfo.getSqlCommandType())) {
            throw new RuntimeException("Statement is not insert: " + statementId);
        }
        return executor.update(statementInfo, parameter);
    }

}
