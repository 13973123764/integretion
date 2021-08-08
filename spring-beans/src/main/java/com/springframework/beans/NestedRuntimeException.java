package com.springframework.beans;

import com.springframework.lang.Nullable;

/**
 * @author: zfan
 * @create: 2021-08-03 13:05
 **/
public abstract class NestedRuntimeException extends RuntimeException{

    /** Use serialVersionUID from Spring 1.2 for interoperability. */
    private static final long serialVersionUID = 5439915454935047936L;

    static {
        // 急切地加载NestedExceptionUtils类，以避免调用getMessage（）时OSGi上的类加载器死锁问题。唐·布朗报道；SPR-5607。
        NestedExceptionUtils.class.getName();
    }

    /**
     * Construct a {@code NestedRuntimeException} with the specified detail message.
     * @param msg the detail message
     */
    public NestedRuntimeException(String msg) {
        super(msg);
    }

    /**
     * Construct a {@code NestedRuntimeException} with the specified detail message
     * and nested exception.
     * @param msg the detail message
     * @param cause the nested exception
     */
    public NestedRuntimeException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }

    @Nullable
    @Override
    public String getMessage() {
        return NestedExceptionUtils.buildMessage(super.getMessage(), getCause());
    }
}
