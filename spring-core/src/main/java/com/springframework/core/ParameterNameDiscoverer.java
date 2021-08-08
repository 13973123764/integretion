package com.springframework.core;

import com.springframework.lang.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author: zfan
 * @create: 2021-08-04 09:44
 **/
public interface ParameterNameDiscoverer {

    @Nullable
    String[] getParameterNames(Method method);

    @Nullable
    String[] getParameterNames(Constructor<?> ctor);

}
