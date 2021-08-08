package com.springframework.core;

import com.springframework.lang.Nullable;
import com.springframework.util.ObjectUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: zfan
 * @create: 2021-08-03 19:21
 **/
final class SerializableTypeWrapper {

    private static final Class<?>[] SUPPORTED_SERIALIZABLE_TYPES = {
            GenericArrayType.class, ParameterizedType.class, TypeVariable.class, WildcardType.class
    };

    static final ConcurrentHashMap<Type, Type> cache = new ConcurrentHashMap<>(256);

    /**
     * 私有化构造器
     */
    private SerializableTypeWrapper(){}

    /**
     * 解包当前类型
     */
    public static <T extends Type> T unwrap(T type) {
        // 获取具体类型
        Type unwrapped = type;
        while (unwrapped instanceof SerializableTypeProxy) {
            unwrapped = ((SerializableTypeProxy) type).getTypeProvider().getType();
        }
        return (unwrapped != null ? (T) unwrapped : type);
    }


    /**
     * 提取具体类型
     * @param provider
     * @return
     */
    @Nullable
    static Type forTypeProvider(TypeProvider provider) {
        // 获取类型
        Type providedType = provider.getType();
        // 不需要序列化类型包装
        if (providedType == null || providedType instanceof Serializable) {
            return providedType;
        }
        // 如果类型在当前运行时环境中通常不可序列化（即使是java.lang.Class本身，例如在Graal上），
        // 那么让我们跳过任何包装尝试
        if (GraalDetector.inImageCode() || !Serializable.class.isAssignableFrom(Class.class)) {
            return providedType;
        }

        // 为给定提供方获得一个序列化类型代理
        // 从缓存中获取类型
        Type cached = cache.get(providedType);
        if (cached != null) {
            return cached;
        }

        // 判断是否是支持的序列化类型
        for (Class<?> type : SUPPORTED_SERIALIZABLE_TYPES) {
            if (type.isInstance(providedType)) {
                // 当前类 类加载器
                ClassLoader classLoader = provider.getClass().getClassLoader();
                Class<?>[] interfaces = new Class<?>[] {type, SerializableTypeProxy.class, Serializable.class};
                // 创建调用处理器
                InvocationHandler handler = new TypeProxyInvocationHandler(provider);
                // 生成代理类
                cached =  (Type) Proxy.newProxyInstance(classLoader, interfaces, handler);
                // 将代理类放入缓存中
                cache.put(providedType, cached);
                return cached;
            }
        }
        throw new IllegalArgumentException("Unsupported Type class: " + providedType.getClass().getName());
    }



    /**
     * 通过代理提供序列化支持和增强任意方法，返回type或type数组
     */
    private static class TypeProxyInvocationHandler implements InvocationHandler, Serializable {

        private final TypeProvider provider;

        public TypeProxyInvocationHandler(TypeProvider provider) {
            this.provider = provider;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 判断equals方法
            if (method.getName().equals("equals") && args != null) {
                // 拿到比较多想
                Object other = args[0];
                // 展开代理
                if (other instanceof Type) {
                    // 获取当前比较对象的具体类型
                    other = unwrap((Type) other);
                }
                // 类型比较
                return ObjectUtils.nullSafeEquals(this.provider.getType(), other);
            }
            // hashCode方法
            else if (method.getName().equals("hashCode")) {
                return ObjectUtils.nullSafeHashCode(this.provider.getType());
            }
            else if (method.getName().equals("getTypeProvider")) {
                return this.provider;
            }

            // 单个类型对象返回的无参方法
            if (Type.class == method.getReturnType() && args == null) {
                return forTypeProvider(new MethodInvokeTypeProvider(this.provider, method, -1));
            }
            // 多个类型对象返回的无参方法
            else if(Type[].class == method.getReturnType() && args == null) {
                Type[] result = new Type[((Type[]) method.invoke(this.provider.getType())).length];
                for (int i = 0; i < result.length; i++) {
                    result[i] = forTypeProvider(new MethodInvokeTypeProvider(this.provider, method, i));
                }
                return result;
            }
            try {
                return method.invoke(this.provider.getType(), args);
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }




    /**
     * 类型访问的接口提供方
     */
    interface TypeProvider extends Serializable {

        @Nullable
        Type getType();

        @Nullable
        default Object getSource() {
            return null;
        }
    }

    /**
     * 序列化类型代理
     */
    interface SerializableTypeProxy {

        /**
         * 返回基础类型提供程序。
         */
        TypeProvider getTypeProvider();
    }

    /**
     * 方法参数类型提供
     */
    static class MethodParameterTypeProvider implements TypeProvider {

        private final String methodName;

        private final Class<?>[] parameterTypes;

        private final Class<?> declaringClass;

        private final int parameterIndex;

        private transient MethodParameter methodParameter;

        public MethodParameterTypeProvider(MethodParameter methodParameter) {
            this.methodName = (methodParameter.getMethod() != null ? methodParameter.getMethod().getName() : null);
            this.parameterTypes = methodParameter.getExecutable().getParameterTypes();
            this.declaringClass = methodParameter.getDeclaringClass();
            this.parameterIndex = methodParameter.getParameterIndex();
            this.methodParameter = methodParameter;
        }

        @Override
        public Type getType() {
            return this.methodParameter.getGenericParameterType();
        }

        @Override
        public Object getSource() {
            return this.methodParameter;
        }

        private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
            inputStream.defaultReadObject();
            try {
                if (this.methodName != null) {
                    this.methodParameter = new MethodParameter(
                            this.declaringClass.getDeclaredMethod(this.methodName, this.parameterTypes), this.parameterIndex);
                }
                else {
                    this.methodParameter = new MethodParameter(
                            this.declaringClass.getDeclaredConstructor(this.parameterTypes), this.parameterIndex);
                }
            }
            catch (Throwable ex) {
                throw new IllegalStateException("Could not find original class structure", ex);
            }
        }
    }


    /**
     * 通过调用无参方法获得类型对象
     */
    static class MethodInvokeTypeProvider implements TypeProvider {

        /** 类型提供 */
        private final TypeProvider provider;

        /** 方法名 */
        private final String methodName;

        /** 定义的类型 */
        private final Class<?> declaringClass;

        private final int index;

        /** 当前方法 */
        private transient Method method;

        @Nullable
        private transient volatile Object result;

        public MethodInvokeTypeProvider(TypeProvider provider, Method method, int index) {
            this.provider = provider;
            this.methodName = method.getName();
            this.declaringClass = method.getDeclaringClass();
            this.index = index;
            this.method = method;
        }

        @Override
        @Nullable
        public Object getSource() {
            return null;
        }

        @Override
        public Type getType() {
            Object result = this.result;
            if (result == null) {

            }
            return null;
        }
    }

    /**
     * 字段类型提供
     */
    static class FieldTypeProvider implements TypeProvider {

        private final String fieldName;

        private final Class<?> declaringClass;

        private transient Field field;

        public FieldTypeProvider(Field field) {
            this.fieldName = field.getName();
            this.declaringClass = field.getDeclaringClass();
            this.field = field;
        }

        @Override
        public Type getType() {
            return this.field.getGenericType();
        }

        @Override
        public Object getSource() {
            return this.field;
        }

        private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
            inputStream.defaultReadObject();
            try {
                this.field = this.declaringClass.getDeclaredField(this.fieldName);
            }
            catch (Throwable ex) {
                throw new IllegalStateException("Could not find original class structure", ex);
            }
        }
    }


}
