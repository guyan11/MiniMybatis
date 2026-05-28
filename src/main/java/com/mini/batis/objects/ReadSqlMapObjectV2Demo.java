package com.mini.batis.objects;

import com.mini.batis.core.XMLConfigBuilder;
import com.mini.batis.model.Configuration;
import com.mini.batis.model.MapperStatement;
import com.mini.batis.reflection.ParamValueResolver;
import com.mini.batis.scripting.ParameterMapping;
import com.mini.batis.scripting.SqlSource;
import com.mini.batis.scripting.SqlSourceBuilder;
import org.apache.commons.dbcp.BasicDataSource;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReadSqlMapObjectV2Demo {

    public static void main(String[] args) throws Exception {
        String configPath = "sqlMapConfig.xml";
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder();
        Configuration configuration = xmlConfigBuilder.parse(configPath);
        if (configuration == null) {
            throw new RuntimeException("configuration is null");
        }


        String statementId = "com.mini.batis.mapper.UserMapper.findByUsername";
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("username", "jane");
        params.put("id", 2);


        MapperStatement statementInfo = configuration.getMapperStatement(statementId);

        if (null == statementInfo) {
            throw new RuntimeException("Can not find statement: " + statementId);
        }

        SqlSourceBuilder sqlSourceBuilder = new SqlSourceBuilder();
        String originalSql = statementInfo.getSql();
        String sql = null;
        SqlSource sqlSource = null;
        List<ParameterMapping> parameterNames = new ArrayList<>();
        if (originalSql.contains("${")) {
            sql = sqlSourceBuilder.parseDollarPlaceholder(originalSql, params);
        }

        if (originalSql.contains("#{")) {
            sqlSource = sqlSourceBuilder.parse(sql == null ? originalSql : sql);
            sql = sqlSource.getBoundSql(params).getSql();
            parameterNames = sqlSource.getBoundSql(params).getParameterMappings();
        }

        if (null == sql && null == sqlSource) {
            return;
        }


        DataSource dataSource = configuration.getDataSource();
        Connection connection = dataSource.getConnection();

        List<Object> resultList = new ArrayList<>();
        String resultType = statementInfo.getResultType();
        Class<?> resultClass = null;
        if (resultType != null && !resultType.trim().isEmpty()) {
            resultClass = Class.forName(resultType);
        }
        if (parameterNames.isEmpty()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Object object = mapToObject(resultSet, resultClass);
                resultList.add(object);
            }
            resultSet.close();
            statement.close();
        } else {
            PreparedStatement statement = connection.prepareStatement(sql);
            for (int i = 0; i < parameterNames.size(); i++) {
                statement.setObject(i + 1, ParamValueResolver.getValue(params,
                        parameterNames.get(i).getProperty()));
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Object object = mapToObject(resultSet, resultClass);
                resultList.add(object);
            }
            resultSet.close();
            statement.close();
        }
        connection.close();
        ((BasicDataSource) dataSource).close();
        System.out.println("resultList =:");
        for (Object o : resultList) {
            System.out.println(o);
        }

    }

    private static Object mapToObject(ResultSet resultSet, Class<?> resultClass) {
        try {
            Object o = resultClass.newInstance();
            Field[] declaredFields = resultClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                String fieldName = declaredField.getName();
                Object value = resultSet.getObject(fieldName);
                if (null == value) {
                    continue;
                }
                declaredField.setAccessible(true);
                declaredField.set(o, value);
            }
            return o;
        } catch (Exception e) {
            return null;
        }
    }


}
