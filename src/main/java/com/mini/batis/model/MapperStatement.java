package com.mini.batis.model;

import com.mini.batis.scripting.SqlSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MapperStatement {

    private String id;
    private String namespace;
    private String statementId;
    private String sql;
    private String resultType;
    private String parameterType;
    private String sqlCommandType;
    private String statementType;

    private SqlSource sqlSource;
}
