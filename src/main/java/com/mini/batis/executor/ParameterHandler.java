package com.mini.batis.executor;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface ParameterHandler {

    void setParameter(PreparedStatement preparedStatement) throws SQLException;
}
