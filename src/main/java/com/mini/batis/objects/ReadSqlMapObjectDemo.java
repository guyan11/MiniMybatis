package com.mini.batis.objects;

import com.mini.batis.model.Configuration;
import com.mini.batis.model.MapperStatement;
import org.apache.commons.dbcp.BasicDataSource;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.sql.DataSource;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadSqlMapObjectDemo {

    public static final Pattern HASH_PARAM_PATTERN = Pattern.compile("#\\{([^}]+)}");
    public static final Pattern DOLLAR_PARAM_PATTERN = Pattern.compile("\\$\\{([^}]+)}");

    public static void main(String[] args) throws Exception {

        Configuration configuration = new Configuration();

        String configPath = "sqlMapConfig.xml";

        InputStream is = ReadSqlMapObjectDemo.class.getClassLoader().getResourceAsStream(configPath);

        if (null == is) {
            throw new RuntimeException("Can not find config file: " + configPath);
        }

        SAXReader saxReader = new SAXReader();
        Document configDocument = saxReader.read(is);
        if (null == configDocument) {
            throw new RuntimeException("Can not parse config file: " + configPath);
        }
        Element configurationElement = configDocument.getRootElement();


        Element environmentsElement = configurationElement.element("environments");
        String environDefault = environmentsElement.attributeValue("default");
        List<Element> elements = environmentsElement.elements("environment");
        Element matchedEnvironmentElement = null;
        for (Element element : elements) {
            String id = element.attributeValue("id");
            if (Objects.equals(id, environDefault)) {
                matchedEnvironmentElement = element;
                break;
            }
        }

        if (null == matchedEnvironmentElement) {
            throw new RuntimeException("Can not find default environment: " + environDefault);
        }

        Element dataSourceElement = matchedEnvironmentElement.element("dataSource");
        String dataSourceType = dataSourceElement.attributeValue("type");

        Properties properties = new Properties();
        if (Objects.equals(dataSourceType, "DBCP")) {
            List<Element> propertyElements = dataSourceElement.elements("property");
            for (Element propertyElement : propertyElements) {
                String name = propertyElement.attributeValue("name");
                String value = propertyElement.attributeValue("value");
                properties.setProperty(name, value);
            }
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName(properties.get("driver").toString());
            dataSource.setUrl(properties.get("url").toString());
            dataSource.setUsername(properties.get("username").toString());
            dataSource.setPassword(properties.get("password").toString());
            configuration.setDataSource(dataSource);
        }

        List<Element> mappersElement = configurationElement.elements("mapper");

        if (null == mappersElement) {
            throw new RuntimeException("Can not find mapper config");
        }

        for (Element element : mappersElement) {
            String resource = element.attributeValue("resource");
            InputStream inputStream = ReadSqlMapObjectDemo.class.getClassLoader().getResourceAsStream(resource);

            if (inputStream == null) {
                throw new RuntimeException("Can not find mapper file: " + resource);
            }

            Document mapperDocument = saxReader.read(inputStream);

            Element mapperRootElement = mapperDocument.getRootElement();

            String namespace = mapperRootElement.attributeValue("namespace");

            if (null == namespace || namespace.trim().isEmpty()) {
                throw new RuntimeException("namespace is empty");
            }

            List<Element> selectElements = mapperRootElement.elements("select");

            for (Element selectElement : selectElements) {
                String id = selectElement.attributeValue("id");
                String resultType = selectElement.attributeValue("resultType");
                String parameterType = selectElement.attributeValue("parameterType");
                String sqlCommandType = selectElement.attributeValue("sqlCommandType");
                String statementType = selectElement.attributeValue("statementType");
                String sql = selectElement.getText();
                Class<?> resultClazz = null;
                if (resultType != null && !resultType.trim().isEmpty()) {
                    resultClazz = Class.forName(resultType);
                }
                Class<?> parameterClazz = null;
                if (parameterType != null && !parameterType.trim().isEmpty()) {
                    parameterClazz = Class.forName(parameterType);
                }
                MapperStatement mapperStatement = new MapperStatement(id, namespace, namespace + "." + id, sql, resultClazz,
                        parameterClazz, sqlCommandType, statementType);
                configuration.addMapperStatement(namespace, mapperStatement);
            }

            String statementId = "com.mini.batis.mapper.UserMapper.findByUsername";
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("username", "jane");
            params.put("id", 2);


            MapperStatement statementInfo = configuration.getMapperStatement(statementId);

            if (null == statementInfo) {
                throw new RuntimeException("Can not find statement: " + statementId);
            }

            String originalSql = statementInfo.getSql();

            List<String> parameterNames = new ArrayList<>();
            String jdbcSql = parseHashPlaceholder(originalSql, parameterNames);
            jdbcSql = parseDollarPlaceholder(jdbcSql, params);


            DataSource dataSource = configuration.getDataSource();
            Connection connection = dataSource.getConnection();

            List<Object> resultList = new ArrayList<>();
            Class<?> resultClass = statementInfo.getResultType();
            if (parameterNames.isEmpty()) {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(jdbcSql);
                while (resultSet.next()) {
                    Object object = mapToObject(resultSet, resultClass);
                    resultList.add(object);
                }
                resultSet.close();
                statement.close();
            } else {
                PreparedStatement statement = connection.prepareStatement(jdbcSql);
                for (int i = 0; i < parameterNames.size(); i++) {
                    statement.setObject(i + 1, getParamValue(params, parameterNames.get(i)));
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

    private static String parseDollarPlaceholder(String jdbcSql, Map<String, Object> params) {
        Matcher matcher = DOLLAR_PARAM_PATTERN.matcher(jdbcSql);
        StringBuffer parsedSql = new StringBuffer();
        while (matcher.find()) {
            String parameterName = matcher.group(1).trim();
            Object parameterValue = getParamValue(params, parameterName);
            String replaceValue = parameterValue == null ? " " : String.valueOf(parameterValue);
            matcher.appendReplacement(parsedSql, Matcher.quoteReplacement(replaceValue));
        }
        matcher.appendTail(parsedSql);
        return parsedSql.toString();
    }

    private static Object getParamValue(Object params, String parameterName) {
        if (null == params) {
            return null;
        }

        if (params instanceof Map) {
            Map<?, ?> paramMap = (Map<?, ?>) params;
            return paramMap.get(parameterName);
        }

        if (isSimpleValue(params)) {
            return params;
        }

        String getterMethodName = "get" + parameterName.substring(0, 1).toUpperCase() + parameterName.substring(1);
        try {
            return params.getClass().getMethod(getterMethodName).invoke(params);
        } catch (Exception e) {
            try {
                Field declaredField = params.getClass().getDeclaredField(parameterName);
                declaredField.setAccessible(true);
                return declaredField.get(params);
            } catch (Exception ex) {
                throw new RuntimeException("Can not get parameter value: " + parameterName);
            }
        }
    }

    private static String parseHashPlaceholder(String originalSql, List<String> parameterNames) {
        Matcher matcher = HASH_PARAM_PATTERN.matcher(originalSql);
        StringBuffer parsedSql = new StringBuffer();
        while (matcher.find()) {
            String paramName = matcher.group(1).trim();
            parameterNames.add(paramName);
            matcher.appendReplacement(parsedSql, "?");
        }
        matcher.appendTail(parsedSql);
        return parsedSql.toString();
    }

    private static boolean isSimpleValue(Object value) {
        return value instanceof String
                || value instanceof Number
                || value instanceof Boolean
                || value instanceof Character;
    }
}
