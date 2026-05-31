package com.mini.batis.session;

import java.util.List;

public interface SqlSession {

    <E> List<E> selectList(String statementId, Object parameter);

    <E> E selectOne(String statementId, Object parameter);

}
