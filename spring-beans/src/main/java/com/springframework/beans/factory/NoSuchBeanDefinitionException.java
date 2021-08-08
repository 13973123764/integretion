package com.springframework.beans.factory;

import com.springframework.beans.BeansException;
import com.springframework.core.ResolvableType;
import com.springframework.lang.Nullable;

/**
 * @author: zfan
 * @create: 2021-08-03 22:14
 **/
public class NoSuchBeanDefinitionException extends BeansException {

    @Nullable
    private final String beanName;

    @Nullable
    private final ResolvableType resolvableType;

    public NoSuchBeanDefinitionException(String name) {
        super("No bean named '" + name + "' available");
        this.beanName = name;
        this.resolvableType = null;
    }

















}
