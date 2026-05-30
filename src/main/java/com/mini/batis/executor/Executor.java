package com.mini.batis.executor;

import com.mini.batis.model.MapperStatement;

import java.util.List;

public interface Executor {

    <E> List<E> query(MapperStatement mapperStatement, Object parameter);
}
