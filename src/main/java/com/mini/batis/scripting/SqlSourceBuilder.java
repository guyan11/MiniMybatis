package com.mini.batis.scripting;

import com.mini.batis.reflection.ParamValueResolver;
import com.mini.batis.scripting.defaults.StaticSqlSource;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlSourceBuilder {

    public static final Pattern HASH_PARAM_PATTERN = Pattern.compile("#\\{([^}]+)}");
    public static final Pattern DOLLAR_PARAM_PATTERN = Pattern.compile("\\$\\{([^}]+)}");


    public SqlSource parse(String originalSql) {
        List<ParameterMapping> parameterMappings = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        Matcher matcher = HASH_PARAM_PATTERN.matcher(originalSql);
        while (matcher.find()) {
            String property = matcher.group(1);
            parameterMappings.add(new ParameterMapping(property));
            matcher.appendReplacement(sb, "?");
        }
        matcher.appendTail(sb);
        return new StaticSqlSource(sb.toString(), parameterMappings);
    }

    public String parseDollarPlaceholder(String originalSql, Object parameterObject) {
        Matcher matcher = DOLLAR_PARAM_PATTERN.matcher(originalSql);
        StringBuffer parsedSql = new StringBuffer();
        while (matcher.find()) {
            String property = matcher.group(1).trim();
            Object value = ParamValueResolver.getValue(parameterObject, property);
            matcher.appendReplacement(parsedSql, value.toString());
        }
        matcher.appendTail(parsedSql);
        return parsedSql.toString();
    }

}
