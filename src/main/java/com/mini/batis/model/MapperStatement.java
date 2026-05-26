package com.mini.batis.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MapperStatement {

    private String id;
    private String namespace;
    private String statementId;
    private String sql;
    private Class<?> resultType;
    private Class<?> parameterType;
    private String sqlCommandType;
    private String statementType;
}
