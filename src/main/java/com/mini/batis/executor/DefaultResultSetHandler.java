package com.mini.batis.executor;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DefaultResultSetHandler implements ResultSetHandler {

    private final Class<?> resultClass;

    public DefaultResultSetHandler(Class<?> resultClass) {
        this.resultClass = resultClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> List<E> handleResultSet(ResultSet rs) throws Exception {
        List<E> resList = new ArrayList<>();
        while (rs.next()) {
            Object res = mapToRow(rs);
            resList.add((E) res);
        }
        return resList;
    }

    private Object mapToRow(ResultSet rs) throws Exception {
        Object res = resultClass.newInstance();
        Field[] declaredFields = res.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Object value = getColumnValue(rs, declaredField.getName());
            if (null == value) {
                continue;
            }
            declaredField.setAccessible(true);
            declaredField.set(res, value);
        }
        return res;
    }

    private static Object getColumnValue(ResultSet rs, String columnName) throws Exception {
        return rs.getObject(columnName);
    }
}
