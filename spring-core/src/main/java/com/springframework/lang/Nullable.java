package com.springframework.lang;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierNickname;
import javax.annotation.meta.When;
import java.lang.annotation.*;

/**
 * 一个通用的注解，用来定义在某些情况下被注解的元素可以为null
 * <p>利用JSR-305元注释在Java中指示可为空的公共值
 * 支持JSR-305的工具，Kotlin使用这些工具推断SpringAPI的可空性。
 *
 * <p>应在参数、返回值和字段级别使用。方法重写应该
 * 重复父{@code @Nullable}注释，除非它们的行为不同。
 *
 * <p>可与{@code @NonNullApi}或{@code @NonNullFields}结合使用，以
 * 将默认的不可为null的语义重写为可为null。
 * @author: zfan
 * @create: 2021-08-03 13:13
 **/
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Nonnull(when = When.MAYBE)
@TypeQualifierNickname
public @interface Nullable {
}
