package com.mini.batis.reflection;

import java.lang.reflect.Field;
import java.util.Map;

public class ParamValueResolver {

    public static Object getValue(Object parameterObject, String property) {
        if (parameterObject == null || property == null) {
            return null;
        }
        if (parameterObject instanceof Map) {
            Map<?, ?> paramMap = (Map<?, ?>) parameterObject;
            return paramMap.get(property);
        }

        if (isSimpleType(parameterObject)) {
            return parameterObject;
        }

        String getterMethodName = "get" + property.substring(0, 1).toUpperCase() + property.substring(1);
        try {
            return parameterObject.getClass().getMethod(getterMethodName).invoke(parameterObject);
        } catch (Exception e) {
            try {
                Field field = parameterObject.getClass().getDeclaredField(property);
                field.setAccessible(true);
                return field.get(parameterObject);
            } catch (Exception ex) {
                throw new RuntimeException("Can not get property value: " + property);
            }
        }
    }

    private static boolean isSimpleType(Object parameterObject) {
        return parameterObject instanceof String ||
                parameterObject instanceof Number ||
                parameterObject instanceof Boolean ||
                parameterObject instanceof Character;
    }
}
