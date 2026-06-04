package com.mini.batis.session;

import com.mini.batis.model.MapperStatement;

import java.util.List;

public interface SqlSession {

    <E> List<E> selectList(String statementId, Object parameter);

    <E> E selectOne(String statementId, Object parameter);

    int insert(String statementId, Object parameter);

    <T> T getMapper(Class<T> mapperClass);

    MapperStatement getMapperStatement(String statementId);

}
