package com.mini.batis.binding;

import java.lang.reflect.Method;
import java.util.Collection;

public class MethodSignature {
    private final Class<?> returnType;

    public MethodSignature(Method method) {
        this.returnType = method.getReturnType();
    }

    public boolean returnMany() {
        return Collection.class.isAssignableFrom(returnType);
    }
}