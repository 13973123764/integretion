package com.springframework.util;

import java.lang.reflect.Modifier;

/**
 * @author: zfan
 * @create: 2021-08-04 09:59
 **/
public abstract class ClassUtils {

    public static boolean isInnerClass(Class<?> clazz) {
        return (clazz.isMemberClass() && Modifier.isStatic(clazz.getModifiers()));
    }

}
