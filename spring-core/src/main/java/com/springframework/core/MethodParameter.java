package com.springframework.core;

import com.springframework.lang.Nullable;
import com.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Map;

/**
 * @author: zfan
 * @create: 2021-08-04 09:15
 **/
public class MethodParameter {

    private static final Annotation[] empty_annotation_array = new Annotation[0];

    private final Executable executable;

    private final int parameterIndex;

    @Nullable
    private volatile Parameter parameter;

    private int nestingLevel;

    /** Map from Integer level to Integer type index. */
    @Nullable
    Map<Integer, Integer> typeIndexesPerLevel;

    @Nullable
    private volatile Class<?> containingClass;

    @Nullable
    private volatile Class<?> parameterType;

    @Nullable
    private volatile Type genericParameterType;

    @Nullable
    private volatile Annotation[] parameterAnnotations;

    @Nullable
    private volatile ParameterNameDiscoverer parameterNameDiscoverer;

    @Nullable
    private volatile String parameterName;

    @Nullable
    private volatile MethodParameter nestedMethodParameter;

    public MethodParameter(Method method, int parameterIndex) {
        this(method, parameterIndex, 1);
    }

    public MethodParameter(Method method, int parameterIndex, int nestingLevel) {
        this.executable = method;
        this.parameterIndex = validateIndex(method, parameterIndex);
        this.nestingLevel = nestingLevel;
    }

    public MethodParameter(Constructor<?> constructor, int parameterIndex) {
        this(constructor, parameterIndex, 1);
    }

    public MethodParameter(Constructor<?> constructor, int parameterIndex, int nestingLevel) {
        this.executable = constructor;
        this.parameterIndex = validateIndex(constructor, parameterIndex);
        this.nestingLevel = nestingLevel;
    }

    private static int validateIndex(Executable executable, int parameterIndex) {
        executable.getParameterCount();
        return parameterIndex;
    }

    public MethodParameter(MethodParameter original) {
        this.executable = original.executable;
        this.parameterIndex = original.parameterIndex;
        this.parameter = original.parameter;
        this.nestingLevel = original.nestingLevel;
        this.typeIndexesPerLevel = original.typeIndexesPerLevel;
        this.containingClass = original.containingClass;
        this.parameterType = original.parameterType;
        this.genericParameterType = original.genericParameterType;
        this.parameterAnnotations = original.parameterAnnotations;
        this.parameterNameDiscoverer = original.parameterNameDiscoverer;
        this.parameterName = original.parameterName;
    }

    @Nullable
    public Method getMethod() {
        return (this.executable instanceof Method ? (Method) this.executable : null);
    }

    public Executable getExecutable() {
        return this.executable;
    }

    /**
     * Return the class that declares the underlying Method or Constructor.
     */
    public Class<?> getDeclaringClass() {
        return this.executable.getDeclaringClass();
    }

    public int getParameterIndex() {
        return this.parameterIndex;
    }

    public Type getGenericParameterType() {
        // 当前参数类型
        Type paramType = this.genericParameterType;
        if (paramType == null) {
            if (this.parameterIndex < 0) {
                Method method = getMethod();
                paramType = (method != null ? method.getGenericReturnType() : void.class);
            }
            else {
                Type[] genericParameterTypes = this.executable.getGenericParameterTypes();
                int index = this.parameterIndex;
                if (this.executable instanceof Constructor &&
                        ClassUtils.isInnerClass(this.executable.getDeclaringClass()) &&
                        genericParameterTypes.length == this.executable.getParameterCount() - 1) {
                    // Bug in javac: type array excludes enclosing instance parameter
                    // for inner classes with at least one generic constructor parameter,
                    // so access it with the actual parameter index lowered by 1
                    index = this.parameterIndex - 1;
                }
                paramType = (index >= 0 && index < genericParameterTypes.length ?
                        genericParameterTypes[index] : getParameterType());
            }
            this.genericParameterType = paramType;
        }
        return paramType;
    }


    public Class<?> getParameterType() {
        Class<?> paramType = this.parameterType;
        if (paramType == null) {
            if (this.parameterIndex < 0) {
                Method method = getMethod();
                paramType = (method != null ? method.getReturnType() : void.class);
            }
            else {
                paramType = this.executable.getParameterTypes()[this.parameterIndex];
            }
            this.parameterType = paramType;
        }
        return paramType;
    }


}
