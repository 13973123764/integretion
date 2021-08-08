package com.springframework.core;

import com.springframework.core.SerializableTypeWrapper.TypeProvider;
import com.springframework.lang.Nullable;
import com.springframework.util.Assert;
import com.springframework.util.ConcurrentReferenceHashMap;

import java.io.Serializable;
import java.lang.reflect.*;

/**
 * 封装Java{@link Java.lang.reflect.Type}，
 * 提供对{@link}getSuperType（）supertypes}、{@link}getInterfaces（）interfaces}和{@link#getGeneric（int..）generic parameters}的访问，
 * 以及最终{@link#resolve（）resolve}到{@link Java.lang.Class}的能力。
 * @author: zfan
 * @create: 2021-08-03 19:16
 **/
public class ResolvableType implements Serializable {

    public static final ResolvableType NONE = new ResolvableType(EmptyType.INSTANCE, null, null, 0);

    private static final ResolvableType[] EMPTY_TYPES_ARRAY = new ResolvableType[0];

    private static final ConcurrentReferenceHashMap<ResolvableType, ResolvableType> cache = new ConcurrentReferenceHashMap<>(256);

    /**
     * java底层类型管理
     */
    private final Type type;

    /**
     * 类型提供
     */
    @Nullable
    private final TypeProvider typeProvider;

    /**
     * 变量解析
     */
    @Nullable
    private final VariableResolver variableResolver;

    /**
     * 数组的组件类型或{@code null}(如果应该推断该类型)。
     */
    private final ResolvableType componentType;

    private final Integer hash;

    private Class<?> resolved;


    /**
     * 私有化构造器 目的是创建一个无需缓存ResolveType对象
     * @param type
     * @param typeProvider
     * @param variableResolver
     * @param hash
     */
    private ResolvableType(Type type, @Nullable SerializableTypeWrapper.TypeProvider typeProvider,
                           @Nullable VariableResolver variableResolver, @Nullable Integer hash) {

        this.type = type;
        this.typeProvider = typeProvider;
        this.variableResolver = variableResolver;
        this.componentType = null;
        this.hash = hash;
        this.resolved = resolveClass();
    }


    /**
     * 解析类
     */
    private Class<?> resolveClass() {
        if (this.type == EmptyType.INSTANCE) {
            return null;
        }
        if (this.type instanceof Class) {
            return (Class<?>) this.type;
        }
        if (this.type instanceof GenericArrayType) {
            Class<?> resolvedComponent = getComponentType().resolve();
            return (resolvedComponent != null ? Array.newInstance(resolvedComponent, 0).getClass() : null);
        }
        return resolveType().resolve();
    }


    /**
     * 获取组件类型
     */
    public ResolvableType getComponentType() {
        if (this == NONE) {
            return NONE;
        }
        if (this.componentType != null) {
            return this.componentType;
        }
        if (this.type instanceof Class) {
            Class<?> componentType = ((Class<?>) this.type).getComponentType();
            return forType(componentType, this.variableResolver);
        }
        if (this.type instanceof GenericArrayType) {
            return forType(((GenericArrayType) this.type).getGenericComponentType(), this.variableResolver);
        }
        return resolveType().getComponentType();
    }


    public static ResolvableType forField(Field field) {
        Assert.notNull(field, "Field must not be null");
        return forType(nu);
    }

    @Nullable
    private ResolvableType resolveVariable(TypeVariable<?> variable) {

    }

    /**
     * 通过单个级别解析此类型，返回解析值或{@link #NONE}。
     * <p>注意:返回的{@link ResolvableType}只能用作中介
     * 因为它不能序列化。
     */
    ResolvableType resolvableType() {
        //
        if (this.type instanceof ParameterizedType) {

        }
    }

    /**
     * 为指定的{@link Type}返回一个{@link ResolvableType}
     */
    static ResolvableType forType(@Nullable Type type, @Nullable TypeProvider typeProvider, @Nullable VariableResolver variableResolver) {

        // type类型为空，typeProvider 不为空时
        if (type == null && typeProvider != null) {
            // 提取类型
            type = SerializableTypeWrapper.forTypeProvider(typeProvider);
        }
        if (type == null) {
            return NONE;
        }

        // 对于简单类的引用, 构建包装
        // 无需昂贵解析的必要，所以不做缓存
        if (type instanceof Class) {
            return new ResolvableType(type, typeProvider, variableResolver, null);
        }

    }

    static ResolvableType forType(@Nullable Type type, @Nullable VariableResolver variableResolver) {
        return forType(type, null, variableResolver);
    }


    /**
     * 用来解析 TypeVariables 的策略接口
     */
    interface VariableResolver extends Serializable {

        /**
         * 返回解析后的资源
         */
        Object getSource();

        /**
         * 解析自定义变量
         */
        ResolvableType resolvableType(TypeVariable<?> variable);
    }

    /**
     * 默认变量解析器
     */
    private class DefaultVariableResolver implements VariableResolver {

        @Override
        public Object getSource() {
            return ResolvableType.this;
        }

        /**
         * 解析变量类型
         * @param variable  变量
         */
        @Override
        public ResolvableType resolvableType(TypeVariable<?> variable) {
            return ResolvableType.this.resolveVariable(variable);
        }
    }


    /**
     * 空的类型
     */
    static class EmptyType implements Type, Serializable {
        static final Type INSTANCE = new EmptyType();

        Object readResolve() {return INSTANCE;}

        public static void main(String[] args) {
            System.out.println("INSTANCE.getTypeName() = " + INSTANCE.getTypeName());
        }
    }
}
