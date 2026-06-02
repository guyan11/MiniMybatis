package com.mini.batis.executor;

import java.sql.Connection;
import java.util.List;

public interface StatementHandler {

    <E> List<E> query(Connection connection);

    int update(Connection connection);
}
